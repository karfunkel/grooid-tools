package groovy.android

import org.junit.runner.RunWith
import org.robolectric.Robolectric
import pl.polidea.robospock.internal.RoboSputnik
import spock.lang.Specification
import spock.lang.Unroll

@RunWith(RoboSputnik)
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
        name | closure | result
        'dp' | { -> dp(16) } | 16
        'sp' | { -> sp(16) } | 16
        'pt' | { -> pt(16) } | 53
        'mm' | { -> mm(16) } | 151
        'inch' | { -> inch(16) } | 3840
    }


}
