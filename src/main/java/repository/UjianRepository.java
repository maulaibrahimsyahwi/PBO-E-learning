package repository;

import model.Ujian;
import java.util.*;

public class UjianRepository {

    private List<Ujian> ujianList = new ArrayList<>();

    public void addUjian(Ujian u) {
        ujianList.add(u);
    }

    public List<Ujian> getAll() {
        return ujianList;
    }

    public Ujian findById(String id) {
        for (Ujian u : ujianList) {
            if (u.getIdUjian().equals(id)) {
                return u;
            }
        }
        return null;
    }
}
