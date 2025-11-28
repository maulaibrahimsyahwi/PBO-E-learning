package repository;

import model.Materi;
import java.util.*;

public class MateriRepository {

    private List<Materi> materiList = new ArrayList<>();

    public void addMateri(Materi m) {
        materiList.add(m);
    }

    public List<Materi> getAll() {
        return materiList;
    }

    public Materi findById(String id) {
        for (Materi m : materiList) {
            if (m.getIdMateri().equals(id)) {
                return m;
            }
        }
        return null;
    }
}
