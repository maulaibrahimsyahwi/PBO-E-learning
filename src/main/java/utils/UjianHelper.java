package utils;

import model.Soal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UjianHelper {

    public static List<Soal> randomizeSoal(List<Soal> originalList, int maxSoal) {
        List<Soal> shuffled = new ArrayList<>(originalList);
        Collections.shuffle(shuffled);
        if (maxSoal > 0 && maxSoal < shuffled.size()) {
            return shuffled.subList(0, maxSoal);
        }
        return shuffled;
    }

    public static Soal randomizeOptions(Soal s) {
        if (!"PG".equalsIgnoreCase(s.getTipeSoal())) return s;

        String key = s.getKunciJawaban();
        String correctContent = "";
        if ("A".equalsIgnoreCase(key)) correctContent = s.getPilA();
        else if ("B".equalsIgnoreCase(key)) correctContent = s.getPilB();
        else if ("C".equalsIgnoreCase(key)) correctContent = s.getPilC();
        else if ("D".equalsIgnoreCase(key)) correctContent = s.getPilD();

        List<String> opts = new ArrayList<>(Arrays.asList(s.getPilA(), s.getPilB(), s.getPilC(), s.getPilD()));
        Collections.shuffle(opts);

        int newIndex = opts.indexOf(correctContent);
        String newKey = switch (newIndex) {
            case 0 -> "A";
            case 1 -> "B";
            case 2 -> "C";
            case 3 -> "D";
            default -> key;
        };

        return new Soal(s.getIdSoal(), s.getIdUjian(), s.getTipeSoal(), s.getPertanyaan(),
                opts.get(0), opts.get(1), opts.get(2), opts.get(3),
                newKey, s.getGambar());
    }
}