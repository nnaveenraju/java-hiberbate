
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DBPersistence2Test {

@Test
public void testDBPersis(){

    DataService dataServiceMock = mock(DataService.class);
    when(dataServiceMock.retrieveAllData()).thenReturn(new int[] {
            24,
            15,
            3
    });

}

}
