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

    public List<Jawaban> findByTugas(String idTugas) {
        List<Jawaban> hasil = new ArrayList<>();
        for (Jawaban j : jawabanList) {
            if (j.getTugas() != null &&
                j.getTugas().getIdTugas().equals(idTugas)) {
                hasil.add(j);
            }
        }
        return hasil;
    }

    public List<Jawaban> findBySiswa(String idSiswa) {
        List<Jawaban> hasil = new ArrayList<>();
        for (Jawaban j : jawabanList) {
            if (j.getSiswa() != null &&
                j.getSiswa().getIdUser().equals(idSiswa)) {
                hasil.add(j);
            }
        }
        return hasil;
    }
}
