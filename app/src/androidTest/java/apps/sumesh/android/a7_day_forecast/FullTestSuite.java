package apps.sumesh.android.a7_day_forecast;

        import android.test.suitebuilder.TestSuiteBuilder;

        import junit.framework.Test;

/**
 * Created by Sumesh on 13-05-2017.
 */

public class FullTestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class).includeAllPackagesUnderHere().build();
    }

    public FullTestSuite(){
        super();
    }


}
