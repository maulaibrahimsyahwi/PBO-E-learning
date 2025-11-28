package service;

import model.Jawaban;
import java.util.*;

public class JawabanService {

    private List<Jawaban> jawabanList = new ArrayList<>();

    public void tambahJawaban(Jawaban j) {
        jawabanList.add(j);
    }

    public List<Jawaban> getJawaban() {
        return jawabanList;
    }
}
