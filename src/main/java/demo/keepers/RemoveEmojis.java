package demo.keepers;

import com.vdurmont.emoji.EmojiParser;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.apache.commons.lang.math.IntRange;

public class RemoveEmojis {

    private static final IntRange EMOJIS_1 = new IntRange(0x1f300, 0x1f64f);
    private static final IntRange EMOJIS_2 = new IntRange(0x1f680, 0x1f6ff);

    public static void main(String[] args) {

        String str = "An 😀awesome 😃string with a few 😉emojis!";

        loopAverageCompare(() -> removeEmojis(str));
        loopAverageCompare(() -> removeEmojis2(str));
        loopAverageCompare(() -> EmojiParser.removeAllEmojis(str));

        System.out.println(EmojiParser.removeAllEmojis(str)); // average speed 150000 nanoseconds
        System.out.println(removeEmojis(str)); // average speed below 10000 nanoseconds
        System.out.println(removeEmojis2(str)); // average speed 15000 nanoseconds

        System.out.println(removeAllEmojis(List.of(str)));
    }

    public static List<String> removeAllEmojis(List<String> data) {
        return data.stream().map(RemoveEmojis::removeEmojis).collect(Collectors.toList());
    }

    public static String removeEmojis(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        text.codePoints().forEach(v -> {
            if (!EMOJIS_1.containsInteger(v) && !EMOJIS_2.containsInteger(v)) {
                stringBuilder.appendCodePoint(v);
            }
        });
        return stringBuilder.toString();
    }

    public static String removeEmojis2(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        text.codePoints()
                .filter(v -> !EMOJIS_1.containsInteger(v) && !EMOJIS_2.containsInteger(v))
                .forEach(stringBuilder::appendCodePoint);
        return stringBuilder.toString();
    }

    // method to measure the execution speed for 1000 iterations
    public static void loopAverageCompare(Supplier<String> supplier) {
        double average = LongStream.range(1, 1000).map(i -> timeCompare(supplier)).average().orElseThrow();
        System.out.println(average);
    }

    public static long timeCompare(Supplier<String> supplier) {
        long l = System.nanoTime();
        supplier.get();
        return System.nanoTime() - l;
    }
}
