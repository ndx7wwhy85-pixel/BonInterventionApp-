# 🔧 GUIDA RISOLUZIONE PROBLEMI - Android Studio

## ⚠️ PROBLEMI COMUNI E SOLUZIONI

### 1. Errore: "Gradle sync failed"

**Sintomo:**
```
Could not resolve all dependencies
Unable to resolve dependency
```

**SOLUZIONE:**
1. Chiudi Android Studio
2. Elimina le cartelle:
   - `.gradle` (nella home del progetto)
   - `.idea` (nella home del progetto)  
   - `app/build`
3. Riapri Android Studio
4. File → Invalidate Caches / Restart → Invalidate and Restart
5. Attendi il sync automatico

---

### 2. Errore: "Namespace not specified"

**Sintomo:**
```
Namespace not specified. Specify a namespace in the module's build.gradle file
```

**SOLUZIONE:**
✅ RISOLTO - Il file `app/build.gradle` ora include:
```gradle
android {
    namespace 'com.bonintervention.app'
    // ...
}
```

---

### 3. Errore: "SDK not found" o "Android SDK location not found"

**SOLUZIONE:**
1. Apri Android Studio
2. File → Project Structure → SDK Location
3. Verifica che il percorso dell'Android SDK sia impostato
4. Se vuoto, clicca "Edit" e scarica l'SDK

**Oppure crea il file `local.properties`:**
```properties
sdk.dir=/Users/[TUO_USERNAME]/Library/Android/sdk
```
(Su Mac - modifica il percorso per il tuo sistema)

---

### 4. Errore: "Unsupported Java version" o "JDK version"

**Sintomo:**
```
Unsupported class file major version
An exception occurred applying plugin request [id: 'com.android.application']
```

**SOLUZIONE:**
1. File → Project Structure → SDK Location
2. Imposta "Gradle JDK" su: **JDK 17** o **Java 17**
3. Se non disponibile, scaricalo da: https://adoptium.net/

---

### 5. Errore: "Missing dependencies" o errori iText

**Sintomo:**
```
Could not find com.itextpdf:itext7-core:7.2.5
Failed to resolve: itext7
```

**SOLUZIONE:**
1. Verifica la connessione internet
2. File → Settings → Build → Gradle
3. Abilita "Offline mode" temporaneamente e poi disabilitalo
4. Sync Project with Gradle Files

**O aggiungi manualmente i repository in `settings.gradle`:**
```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

---

### 6. Errore: "Manifest merger failed"

**Sintomo:**
```
Manifest merger failed with multiple errors
```

**SOLUZIONE:**
Controlla che il file `AndroidManifest.xml` abbia il namespace corretto:
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
```

---

### 7. Errore: "Resource not found" o "Layout inflation error"

**Sintomo:**
```
Resource ID #0x... is not valid
android.view.InflateException: Binary XML file line #XX
```

**SOLUZIONE:**
1. Build → Clean Project
2. Build → Rebuild Project
3. Verifica che tutti i file in `res/` siano corretti

---

### 8. Errore durante l'esecuzione: "App keeps stopping"

**SOLUZIONE:**
1. Controlla il Logcat in Android Studio (finestra in basso)
2. Cerca l'eccezione rossa con lo stack trace
3. Verifica i permessi:
   - Vai in Impostazioni del dispositivo
   - App → Bon d'Intervention → Permessi
   - Abilita Fotocamera e Archiviazione

---

### 9. Errore: "Failed to install APK"

**SINTOMO:**
```
Installation failed with message Failed to establish session
INSTALL_FAILED_INSUFFICIENT_STORAGE
```

**SOLUZIONE:**
- Libera spazio sul dispositivo/emulatore (almeno 100MB)
- Disinstalla la versione precedente dell'app
- Build → Clean Project e poi Run

---

### 10. Errore: "ViewBinding not generated"

**Sintomo:**
```
Unresolved reference: ActivityMainBinding
Cannot access class 'com.bonintervention.app.databinding.ActivityMainBinding'
```

**SOLUZIONE:**
1. Verifica che in `app/build.gradle` ci sia:
```gradle
buildFeatures {
    viewBinding true
}
```
2. Build → Rebuild Project
3. File → Invalidate Caches / Restart

---

## 🚀 PROCEDURA RAPIDA DI TROUBLESHOOTING

Se hai problemi generici, prova questa sequenza:

```bash
1. Build → Clean Project
2. File → Invalidate Caches / Restart → Invalidate and Restart
3. Chiudi Android Studio
4. Elimina: .gradle, .idea, app/build
5. Riapri Android Studio
6. Attendi il sync di Gradle
7. Build → Rebuild Project
8. Run
```

---

## 📋 CHECKLIST PRE-COMPILAZIONE

Prima di compilare, verifica:

- ✅ Android Studio aggiornato (versione 2023.1+)
- ✅ JDK 17 installato e configurato
- ✅ Android SDK installato (API 34 minimo)
- ✅ Connessione internet attiva (per scaricare dipendenze)
- ✅ Almeno 4GB di RAM disponibili
- ✅ Almeno 5GB di spazio su disco

---

## 🆘 SE NULLA FUNZIONA

1. **Esporta il messaggio di errore completo** dal Build Output
2. **Controlla il Logcat** per eccezioni runtime
3. **Verifica la versione di Gradle**: dovrebbe essere 8.2+
4. **Prova a creare un nuovo progetto vuoto** in Android Studio per verificare che l'ambiente funzioni

---

## 📱 VERSIONI TESTATE

Questo progetto è stato testato con:
- Android Studio: Hedgehog 2023.1.1+
- Gradle: 8.2
- JDK: 17
- Android SDK: API 24-34
- Kotlin: 1.9.22

---

## 💡 SUGGERIMENTI

- **Patience**: Il primo sync di Gradle può richiedere 5-10 minuti
- **Internet**: Assicurati di avere una connessione stabile
- **Spazio**: Gradle scarica molte dipendenze (1-2GB)
- **Aggiornamenti**: Mantieni Android Studio aggiornato

---

## 🔍 LOGS UTILI

Per diagnosticare problemi, controlla:

1. **Build Output** (in basso in Android Studio)
2. **Logcat** (per errori runtime)
3. **Event Log** (in basso a destra)
4. **Gradle Console** (per errori di build)
