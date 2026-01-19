# Blocky OUT

**Blocky OUT** este un joc de logică și strategie dezvoltat în **Java**, inspirat din popularul joc de browser [Blocky Out](https://poki.com/ro/g/blocky-out). Obiectivul principal este de a manevra blocurile colorate pentru a elibera calea și a scoate blocul special prin poartă.

## Galerie

<p align="center">
  <img src="Poze Joc/Screenshot 2026-01-19 210705.png" alt="Meniu Principal" width="45%">
  <img src="Poze Joc/Screenshot 2026-01-19 210847.png" alt="Gameplay" width="45%">
</p>

<p align="center">
  <img src="Poze Joc/Screenshot 2026-01-19 211249.png" alt="Level Editor" width="45%">
  <img src="Poze Joc/Screenshot 2026-01-19 211233.png" alt="Selectie Nivele" width="45%">
</p>

## Despre Proiect

Acest proiect reprezintă o implementare complexă a mecanicilor de tip "sliding block puzzle", oferind nu doar nivele predefinite, ci și un editor robust pentru crearea de noi provocări. Aplicația este construită folosind biblioteca standard **Java Swing/AWT** pentru interfața grafică, fără dependențe externe majore.

### Caracteristici Principale

*   **Mod de Joc Clasic:** 12 nivele progresive, fiecare cu dificultate crescândă.
*   **Level Editor Complet:**
    *   Plasare de ziduri, porți și blocuri.
    *   Redimensionare și colorare a blocurilor.
    *   Sistem **Undo/Redo** pentru editare rapidă.
    *   Salvarea și încărcarea hărților personalizate.
    *   Ștergerea hărților cu confirmare (Pop-up).
*   **Sistem de Progres:** Salvarea automată a nivelurilor deblocate și a monedelor colectate.
*   **Mecanici Avansate:**
    *   Coliziuni precise.
    *   Restricții de mișcare (blocuri care se mișcă doar orizontal/vertical).
    *   Limită de timp și sistem de recompense.
*   **Interfață Modernă:**
    *   Design minimalist cu temă întunecată (Dark Mode).
    *   Sistem de particule pentru fundalul meniului.
    *   Butoane interactive cu feedback vizual.
    *   Dialoguri de confirmare pentru acțiuni critice (Reset, Ștergere).

## Tehnologii Utilizate

*   **Limbaj:** Java (JDK 8+)
*   **GUI:** Java Swing & AWT (Graphics2D pentru randare custom)
*   **Design Patterns:**
    *   **Singleton:** `GameEngine` (pentru gestionarea unică a stării jocului).
    *   **Observer:** `IObserver` (pentru actualizarea interfeței grafice la schimbarea stării).
    *   **Strategy:** `IMovementStrategy` (pentru logica de coliziune și mișcare).
    *   **Factory:** `LevelFactory` (pentru generarea nivelurilor).
*   **Stocare:** Java Preferences API (pentru salvarea progresului local).

## Structura Proiectului

```
Blocky OUT/
├── src/com/blocky/
│   ├── main/           # Punctul de intrare (Main)
│   ├── logic/          # Logica jocului (Engine, Collision, LevelFactory)
│   ├── model/          # Entități (Block, ExitGate, Entity)
│   ├── view/           # Interfața grafică (BoardPanel, Particle, Theme)
│   └── interfaces/     # Interfețe pentru abstractizare
├── levels/             # Fișiere text pentru hărți (opțional)
├── Poze Joc/           # Screenshot-uri demonstrative
└── lib/                # Biblioteci (JUnit pentru teste)
```

## Cum se rulează

1.  Asigurați-vă că aveți instalat **Java Development Kit (JDK)**.
2.  Deschideți proiectul într-un IDE (IntelliJ IDEA, Eclipse, VS Code).
3.  Rulați clasa `src/com/blocky/main/Blocky.java`.

## Mai multe imagini

### Editor de Nivele
Editorul permite control total asupra designului nivelului, inclusiv setarea dimensiunilor și restricțiilor de mișcare.

<p align="center">
  <img src="Poze Joc/Screenshot 2026-01-19 211308.png" width="40%">
  <img src="Poze Joc/Screenshot 2026-01-19 211321.png" width="40%">
  <img src="Poze Joc/Screenshot 2026-01-19 210732.png" width="40%">
</p>

### Meniul Pentru un User Nou

<p align="center">
  <img src="Poze Joc/Screenshot 2026-01-19 210721.png" width="80%">
</p>

### Gameplay și Meniuri

<p align="center">
  <img src="Poze Joc/Screenshot 2026-01-19 210912.png" width="40%">
  <img src="Poze Joc/Screenshot 2026-01-19 211012.png" width="40%">
  <img src="Poze Joc/Screenshot 2026-01-19 211220.png" width="40%">
  <img src="Poze Joc/Screenshot 2026-01-19 211454.png" width="40%">
  <img src="Poze Joc/Screenshot 2026-01-19 211519.png" width="40%">
</p>

---
*Proiect realizat ca parte a portofoliului de dezvoltare software.*
