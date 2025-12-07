package model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UjianProgress {
    private String idProgress;
    private String idSiswa;
    private String idUjian;
    private int currentIndex;
    private int sisaWaktu; 
    private int violationCount;
    private String waktuMulai; 
    private Map<Integer, String> jawabanSementara;

    public UjianProgress(String idProgress, String idSiswa, String idUjian, int currentIndex, int sisaWaktu, int violationCount, String waktuMulai, String jawabanStr) {
        this.idProgress = idProgress;
        this.idSiswa = idSiswa;
        this.idUjian = idUjian;
        this.currentIndex = currentIndex;
        this.sisaWaktu = sisaWaktu;
        this.violationCount = violationCount;
        this.waktuMulai = waktuMulai;
        this.jawabanSementara = parseJawaban(jawabanStr);
    }

    public UjianProgress(String idProgress, String idSiswa, String idUjian, int durasiAwal) {
        this.idProgress = idProgress;
        this.idSiswa = idSiswa;
        this.idUjian = idUjian;
        this.currentIndex = 0;
        this.sisaWaktu = durasiAwal;
        this.violationCount = 0;
        this.waktuMulai = LocalDateTime.now().toString();
        this.jawabanSementara = new HashMap<>();
    }

    public String getIdProgress() { return idProgress; }
    public String getIdSiswa() { return idSiswa; }
    public String getIdUjian() { return idUjian; }
    public int getCurrentIndex() { return currentIndex; }
    public int getSisaWaktu() { return sisaWaktu; }
    public int getViolationCount() { return violationCount; }
    public String getWaktuMulai() { return waktuMulai; }
    public Map<Integer, String> getJawabanSementara() { return jawabanSementara; }

    public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }
    public void setSisaWaktu(int sisaWaktu) { this.sisaWaktu = sisaWaktu; }
    public void setViolationCount(int violationCount) { this.violationCount = violationCount; }
    public void addJawaban(int index, String jawab) { jawabanSementara.put(index, jawab); }

    public String getJawabanString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, String> entry : jawabanSementara.entrySet()) {
            if (sb.length() > 0) sb.append("||");
            sb.append(entry.getKey()).append("::").append(entry.getValue());
        }
        return sb.toString();
    }

    private Map<Integer, String> parseJawaban(String str) {
        Map<Integer, String> map = new HashMap<>();
        if (str == null || str.isBlank()) return map;
        String[] pairs = str.split("\\|\\|");
        for (String pair : pairs) {
            String[] kv = pair.split("::");
            if (kv.length == 2) {
                try {
                    map.put(Integer.parseInt(kv[0]), kv[1]);
                } catch (NumberFormatException ignored) {}
            }
        }
        return map;
    }
}