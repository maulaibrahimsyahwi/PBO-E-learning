```
LMS SMK NUSANTARA
├─ data
│  ├─ logs
│  │  ├─ application.log
│  │  ├─ audit.log
│  │  └─ error.log
│  ├─ storage
│  │  └─ materi
│  └─ uploads
├─ hierarchical structure.md
├─ pom.xml
├─ readme.md
├─ src
│  └─ main
│     └─ java
│        ├─ app
│        │  └─ App.java
│        ├─ config
│        │  └─ DatabaseConnection.java
│        ├─ context
│        │  └─ AppContext.java
│        ├─ model
│        │  ├─ Absensi.java
│        │  ├─ Admin.java
│        │  ├─ ForumReply.java
│        │  ├─ ForumThread.java
│        │  ├─ Guru.java
│        │  ├─ GuruAssignment.java
│        │  ├─ Jadwal.java
│        │  ├─ Jawaban.java
│        │  ├─ Kelas.java
│        │  ├─ Mapel.java
│        │  ├─ MataPelajaran.java
│        │  ├─ Materi.java
│        │  ├─ Nilai.java
│        │  ├─ Siswa.java
│        │  ├─ Soal.java
│        │  ├─ Tugas.java
│        │  ├─ Ujian.java
│        │  ├─ UjianProgress.java
│        │  └─ User.java
│        ├─ repository
│        │  ├─ AbsensiRepository.java
│        │  ├─ ForumRepository.java
│        │  ├─ JawabanRepository.java
│        │  ├─ KelasRepository.java
│        │  ├─ MapelRepository.java
│        │  ├─ MateriRepository.java
│        │  ├─ NilaiRepository.java
│        │  ├─ SoalRepository.java
│        │  ├─ TugasRepository.java
│        │  ├─ UjianProgressRepository.java
│        │  ├─ UjianRepository.java
│        │  └─ UserRepository.java
│        ├─ service
│        │  ├─ AuthService.java
│        │  ├─ AutoSaveService.java
│        │  ├─ FileService.java
│        │  ├─ MapelService.java
│        │  ├─ UjianEvaluationService.java
│        │  └─ UjianService.java
│        ├─ utils
│        │  ├─ DateUtil.java
│        │  ├─ IdUtil.java
│        │  ├─ LoadingUtil.java
│        │  ├─ LoggerUtil.java
│        │  ├─ SecurityUtil.java
│        │  ├─ UjianHelper.java
│        │  └─ ValidationUtil.java
│        └─ view
│           ├─ component
│           │  └─ ForumPanel.java
│           ├─ dialog
│           │  ├─ EditGuruAssignmentDialog.java
│           │  ├─ GuruUjianSoalDialog.java
│           │  └─ SiswaUjianDialog.java
│           ├─ GuiAdmin.java
│           ├─ GuiGuru.java
│           ├─ GuiLogin.java
│           ├─ GuiSiswa.java
│           ├─ panel
│           │  ├─ AdminDashboardPanel.java
│           │  ├─ GuruAbsensiPanel.java
│           │  ├─ GuruAssignmentPanel.java
│           │  ├─ GuruManagementPanel.java
│           │  ├─ GuruMateriPanel.java
│           │  ├─ GuruNilaiPanel.java
│           │  ├─ GuruTugasPanel.java
│           │  ├─ GuruUjianPanel.java
│           │  ├─ KelasManagementPanel.java
│           │  ├─ MapelManagementPanel.java
│           │  ├─ SiswaAbsensiPanel.java
│           │  ├─ SiswaManagementPanel.java
│           │  ├─ SiswaMateriPanel.java
│           │  ├─ SiswaNilaiPanel.java
│           │  └─ SiswaTugasUjianPanel.java
│           └─ renderer
│              ├─ GuruListRenderer.java
│              ├─ KelasListRenderer.java
│              ├─ MapelAssignmentRenderer.java
│              └─ SiswaAssignmentRenderer.java
└─ target
   ├─ classes
   │  ├─ app
   │  │  └─ App.class
   │  ├─ config
   │  │  └─ DatabaseConnection.class
   │  ├─ context
   │  │  └─ AppContext.class
   │  ├─ model
   │  │  ├─ Absensi.class
   │  │  ├─ Admin.class
   │  │  ├─ ForumReply.class
   │  │  ├─ ForumThread.class
   │  │  ├─ Guru.class
   │  │  ├─ GuruAssignment.class
   │  │  ├─ Jadwal.class
   │  │  ├─ Jawaban.class
   │  │  ├─ Kelas.class
   │  │  ├─ Mapel.class
   │  │  ├─ MataPelajaran.class
   │  │  ├─ Materi.class
   │  │  ├─ Nilai.class
   │  │  ├─ Siswa.class
   │  │  ├─ Soal.class
   │  │  ├─ Tugas.class
   │  │  ├─ Ujian.class
   │  │  ├─ UjianProgress.class
   │  │  └─ User.class
   │  ├─ repository
   │  │  ├─ AbsensiRepository.class
   │  │  ├─ ForumRepository.class
   │  │  ├─ JawabanRepository.class
   │  │  ├─ KelasRepository.class
   │  │  ├─ MapelRepository.class
   │  │  ├─ MateriRepository.class
   │  │  ├─ NilaiRepository.class
   │  │  ├─ SoalRepository.class
   │  │  ├─ TugasRepository.class
   │  │  ├─ UjianProgressRepository.class
   │  │  ├─ UjianRepository.class
   │  │  └─ UserRepository.class
   │  ├─ service
   │  │  ├─ AuthService.class
   │  │  ├─ AutoSaveService$1.class
   │  │  ├─ AutoSaveService.class
   │  │  ├─ FileService.class
   │  │  ├─ MapelService.class
   │  │  ├─ UjianEvaluationService.class
   │  │  └─ UjianService.class
   │  ├─ utils
   │  │  ├─ DateUtil.class
   │  │  ├─ IdUtil.class
   │  │  ├─ LoadingUtil$1.class
   │  │  ├─ LoadingUtil$2.class
   │  │  ├─ LoadingUtil$3.class
   │  │  ├─ LoadingUtil$LoadingCallback.class
   │  │  ├─ LoadingUtil$LoadingDialog.class
   │  │  ├─ LoadingUtil$ProgressDialog.class
   │  │  ├─ LoadingUtil$ProgressTask.class
   │  │  ├─ LoadingUtil$ProgressUpdater.class
   │  │  ├─ LoadingUtil$TaskResult.class
   │  │  ├─ LoadingUtil.class
   │  │  ├─ LoggerUtil$CustomFormatter.class
   │  │  ├─ LoggerUtil$PerformanceLogger.class
   │  │  ├─ LoggerUtil.class
   │  │  ├─ SecurityUtil.class
   │  │  ├─ UjianHelper.class
   │  │  ├─ ValidationUtil$ValidationBuilder.class
   │  │  └─ ValidationUtil.class
   │  └─ view
   │     ├─ component
   │     │  ├─ ForumPanel$1.class
   │     │  └─ ForumPanel.class
   │     ├─ dialog
   │     │  ├─ EditGuruAssignmentDialog.class
   │     │  ├─ GuruUjianSoalDialog.class
   │     │  ├─ SiswaUjianDialog$1.class
   │     │  ├─ SiswaUjianDialog$2.class
   │     │  └─ SiswaUjianDialog.class
   │     ├─ GuiAdmin.class
   │     ├─ GuiGuru$1.class
   │     ├─ GuiGuru$2.class
   │     ├─ GuiGuru.class
   │     ├─ GuiLogin.class
   │     ├─ GuiSiswa.class
   │     ├─ panel
   │     │  ├─ AdminDashboardPanel.class
   │     │  ├─ GuruAbsensiPanel$1.class
   │     │  ├─ GuruAbsensiPanel.class
   │     │  ├─ GuruAssignmentPanel$1.class
   │     │  ├─ GuruAssignmentPanel$2.class
   │     │  ├─ GuruAssignmentPanel.class
   │     │  ├─ GuruManagementPanel$1.class
   │     │  ├─ GuruManagementPanel$2.class
   │     │  ├─ GuruManagementPanel.class
   │     │  ├─ GuruMateriPanel$1.class
   │     │  ├─ GuruMateriPanel.class
   │     │  ├─ GuruNilaiPanel$1.class
   │     │  ├─ GuruNilaiPanel.class
   │     │  ├─ GuruTugasPanel.class
   │     │  ├─ GuruUjianPanel.class
   │     │  ├─ KelasManagementPanel$1.class
   │     │  ├─ KelasManagementPanel$2.class
   │     │  ├─ KelasManagementPanel.class
   │     │  ├─ MapelManagementPanel$1.class
   │     │  ├─ MapelManagementPanel$2.class
   │     │  ├─ MapelManagementPanel.class
   │     │  ├─ SiswaAbsensiPanel.class
   │     │  ├─ SiswaManagementPanel$1.class
   │     │  ├─ SiswaManagementPanel$2.class
   │     │  ├─ SiswaManagementPanel.class
   │     │  ├─ SiswaMateriPanel$1.class
   │     │  ├─ SiswaMateriPanel.class
   │     │  ├─ SiswaNilaiPanel.class
   │     │  ├─ SiswaTugasUjianPanel$1.class
   │     │  └─ SiswaTugasUjianPanel.class
   │     └─ renderer
   │        ├─ GuruListRenderer.class
   │        ├─ KelasListRenderer.class
   │        ├─ MapelAssignmentRenderer.class
   │        └─ SiswaAssignmentRenderer.class
   └─ test-classes

```
