package technology.positivehome.ihome.server.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import technology.positivehome.ihome.domain.runtime.ChartPoint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class PowerConsumptionStatBuilderTest {

    PowerConsumptionStatBuilder builder;

    @ParameterizedTest
    @MethodSource("testChartPointDataOrderValidatorSrc")
    void calcDiff(List<ChartPoint> chartPoints, List<ChartPoint> expected) {
        builder = new PowerConsumptionStatBuilder();
        Assertions.assertEquals(expected, builder.calcDiff(chartPoints));
    }

    public static Stream<Arguments> testChartPointDataOrderValidatorSrc() {
        return Stream.of(
                Arguments.of(Arrays.asList(
                        ChartPoint.of(LocalDateTime.now().minusMinutes(15), 64147),
                        ChartPoint.of(LocalDateTime.now().minusMinutes(10), 64147),
                        ChartPoint.of(LocalDateTime.now(), 288510)),
                new ArrayList<>()));

    }
}
