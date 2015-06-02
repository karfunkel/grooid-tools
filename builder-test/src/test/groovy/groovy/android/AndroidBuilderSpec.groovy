package groovy.android

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import pl.polidea.robospock.internal.RoboSputnik
import spock.lang.Specification
import spock.lang.Unroll

@RunWith(RoboSputnik)
@Config(manifest = Config.NONE)
class AndroidBuilderSpec extends Specification {

    AndroidBuilder builder

    def setup() {
        builder = new AndroidBuilder(true)
    }

    @Unroll
    def "test #name to px conversion"() {
        expect:
        builder.build(Robolectric.application, closure) == result

        where:
        name   | closure         | result
        'dp'   | { -> dp(16) }   | 16
        'sp'   | { -> sp(16) }   | 16
        'pt'   | { -> pt(16) }   | 53
        'mm'   | { -> mm(16) }   | 151
        'inch' | { -> inch(16) } | 3840
    }

    def "Is the given instance equal to the result?"() {
        when:
        def instance = new TextView(Robolectric.application)

        then:
        builder.build(Robolectric.application) { textView(instance) }.is instance
    }

    def "Check for context"() {
        when:
        builder.textView()
        then:
        Exception ex = thrown()
        ex.cause instanceof IllegalArgumentException
        ex.cause.message == "Please provide a $Context.canonicalName as attribute context in this or a previous layer"
    }

    // TODO: Test context change

    @Unroll
    def "Test padding with #attributes"() {
        when:
        View view = builder.build(Robolectric.application) { textView(attributes) }

        then:
        view.paddingBottom == bottom
        view.paddingEnd == end
        view.paddingLeft == left
        view.paddingRight == right
        view.paddingStart == start
        view.paddingTop == top

        where:
        attributes                                                             | start | left | top | end | right | bottom
        [padding: [10, 20, 30, 40]]                                            | 10    | 10   | 20  | 30  | 30    | 40
        [padding: [10, 20, 30]]                                                | 10    | 10   | 20  | 30  | 30    | 20
        [padding: [10, 20]]                                                    | 10    | 10   | 20  | 10  | 10    | 20
        [padding: [10]]                                                        | 10    | 10   | 10  | 10  | 10    | 10
        [paddingStart: 10, paddingTop: 20, paddingEnd: 30, paddingBottom: 40]  | 10    | 10   | 20  | 30  | 30    | 40
        [paddingLeft: 10, paddingTop: 20, paddingRight: 30, paddingBottom: 40] | 10    | 10   | 20  | 30  | 30    | 40
        [paddingStart: 10, paddingTop: 20, paddingEnd: 30]                     | 10    | 10   | 20  | 30  | 30    | 0
        [paddingLeft: 10, paddingTop: 20, paddingRight: 30]                    | 10    | 10   | 20  | 30  | 30    | 0
        [paddingStart: 10, paddingTop: 20]                                     | 10    | 10   | 20  | 0   | 0     | 0
        [paddingLeft: 10, paddingTop: 20]                                      | 10    | 10   | 20  | 0   | 0     | 0
        [paddingStart: 10]                                                     | 10    | 10   | 0   | 0   | 0     | 0
        [paddingLeft: 10]                                                      | 10    | 10   | 0   | 0   | 0     | 0
        [:]                                                                    | 0     | 0    | 0   | 0   | 0     | 0
    }

    def "Test default tags"() {
        when:
        View view = builder.build(Robolectric.application) {
            textView(tag: "Default tag", (100000000): "Tag for key 1000000000", (200000000.123): "Tag for key 20000000000")
        }
        then:
        view.tag == "Default tag"
        view.getTag(100000000) == "Tag for key 1000000000"
        view.getTag(200000000) == "Tag for key 20000000000"
    }

    def "Test for addListener handling with only one implemented method"() {
        when:
        def view
        TextView textView = builder.build(Robolectric.application) {
            textView(onViewAttachedToWindow: { view = it })
        }
        View.OnAttachStateChangeListener listener = textView.listenerInfo.mOnAttachStateChangeListeners[0]
        listener.onViewAttachedToWindow(textView)

        then:
        view.is textView

        when:
        listener.onViewDetachedFromWindow(textView)

        then:
        notThrown(UnsupportedOperationException)
    }

    def "Test for addListener handling with multiple implemented method"() {
        when:
        def view1
        def view2
        TextView textView = builder.build(Robolectric.application) {
            textView(onViewAttachedToWindow: { view1 = it }, onViewDetachedFromWindow: { view2 = it })
        }
        View.OnAttachStateChangeListener listener = textView.listenerInfo.mOnAttachStateChangeListeners[0]
        listener.onViewAttachedToWindow(textView)

        then:
        view1.is textView

        when:
        listener.onViewDetachedFromWindow(textView)

        then:
        view2.is textView
    }

    def "Test for setListener handling"() {
        when:
        View view
        MotionEvent event
        TextView textView = builder.build(Robolectric.application) {
            textView(onTouch: { v, e ->
                view = v
                event = e
                return true
            })
        }
        View.OnTouchListener listener = textView.listenerInfo.mOnTouchListener
        MotionEvent me = MotionEvent.obtain(10l, 20l, 30, 30.0f, 40.0f, 1)
        listener.onTouch(textView, me)

        then:
        view.is textView
        event.is me
    }

    def "Test id handling"() {
        when:
        View ref
        View view
        builder.build(Robolectric.application) {
            ref = textView(id: 'text')
            view = text
        }

        then:
        ref.is view
        ref.is builder.getVariable('text')
        ref.id == 'text'.hashCode()

        when:
        builder.build(Robolectric.application) {
            ref = textView(id: 1000)
        }

        then:
        ref.is builder.getVariable('id_1000')
        ref.id == 1000
    }

    // TODO: layout properties handling
}
