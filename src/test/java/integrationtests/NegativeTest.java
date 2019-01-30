package integrationtests;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitrunner.E2eJunitRunner;


@TargetEnv("application_host.properties")
@RunWith(E2eJunitRunner.class)
public class NegativeTest {

    @Test
    @JsonTestCase("integration_tests/negative/negative_get_person_by_invalid_id.json")
    public void test_getpersonWithInvalidid(){
    }

    @Test
    @JsonTestCase("integration_tests/negative/negative_bad_url_test.json")
    public void test_badUrl(){
    }

}