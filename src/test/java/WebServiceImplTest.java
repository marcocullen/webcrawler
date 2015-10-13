import com.example.service.WebCrawlerServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WebServiceImplTest {
    WebCrawlerServiceImpl webCrawlerService = new WebCrawlerServiceImpl();


    @Test
    public void wiproRobotParseTest() {
        String testRobtsString = "User-agent: *\nDisallow: /wp-admin/";
        Set<String> expected = new HashSet<>();
        expected.add("/wp-admin");
        Set<String> disallowed = webCrawlerService.getDisallowedPaths(testRobtsString);
        assertThat(disallowed, is(expected));
    }

    @Test
    public void googleRobotParseTest() {
        String testRobtsString = "User-agent: *\nDisallow: /forms/perks/\nDisallow: /baraza/*/search\nDisallow: /baraza/*/report\nDisallow: /shopping/suppliers/search\nDisallow: /ct/\nDisallow: /edu/cs4hs/\nAllow: /search/about";
        Set<String> expected = new HashSet<>();
        expected.add("/forms/perks");
        expected.add("/ct");
        expected.add("/edu/cs4hs");
        Set<String> disallowed = webCrawlerService.getDisallowedPaths(testRobtsString);
        assertThat(disallowed, is(expected));
    }
}
