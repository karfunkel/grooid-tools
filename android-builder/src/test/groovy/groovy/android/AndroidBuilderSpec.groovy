package groovy.android

import spock.lang.Specification

class AndroidBuilderSpec extends Specification {

    AndroidBuilder builder

    def setup() {
        builder = new AndroidBuilder(true)
    }

    def "test the setup"() {
        expect:
        true
    }
}
