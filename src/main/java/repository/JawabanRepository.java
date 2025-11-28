package repository;

import model.Jawaban;
import java.util.*;

public class JawabanRepository {

    private List<Jawaban> jawabanList = new ArrayList<>();

    public void addJawaban(Jawaban j) {
        jawabanList.add(j);
    }

    public List<Jawaban> getAll() {
        return jawabanList;
    }

    public List<Jawaban> findBySiswa(String idSiswa) {
        List<Jawaban> result = new ArrayList<>();
        for (Jawaban j : jawabanList) {
            if (j.getSiswa().getIdUser().equals(idSiswa)) {
                result.add(j);
            }
        }
        return result;
    }
}
