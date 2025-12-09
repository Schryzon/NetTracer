```
┏┓╻┏━╸╺┳╸╺┳╸┏━┓┏━┓┏━╸┏━╸┏━┓
┃┗┫┣╸  ┃  ┃ ┣┳┛┣━┫┃  ┣╸ ┣┳┛
╹ ╹┗━╸ ╹  ╹ ╹┗╸╹ ╹┗━╸┗━╸╹┗╸
Cisco IOS & STP Simulation in Java
```

A **terminal UI** that emulates a slice of Cisco-style IOS for **switch-only labs**: add switches, wire ports, assign costs, compute MST, trace paths, and send ICMP-like pings, all **implemented with custom Data Structures & Algorithms**.

> Course context: University of Mataram — Algorithms & Data Structures / 2025.

Licensed under the **MIT License**.

---

## ✨ What makes NetTracer different

* **Real networking concept**: focuses on **STP** (loop-free logical topology on a loopy physical graph).
* **DSA-first**: custom **LinkedList**, **Stack**, **PriorityQueue**, **Graph arrays**, **Merge Sort**, **Union-Find**, **BFS/Dijkstra**.
* **IOS-like CLI**: `show`, `add-link`, `set-priority`, `ping`, `show ascii mst`, etc.
* **ASCII Topology Renderer**: scalable circular layout, **rectangular switch boxes**, edge connectors from **box boundaries**, MST/shortest-path highlighting.

---

## 🧩 Libraries / Modules Used

| Category       | Module                     | Notes                                            |
| -------------- | -------------------------- | ------------------------------------------------ |
| **Standard**   | `java.lang`                | Implicit (String, Math, etc.)                    |
|                | `java.util.Scanner`        | **Only in CLI shell** for input                  |
| **Custom DSA** | `Topology/LinkedList.java` | Port/link index lists per switch                 |
|                | `CLI/Stack.java`           | Command history (undo/redo infra)                |
|                | `PDU/PriorityQueue.java`   | Event scheduler for ping PDUs                    |
|                | `Topology/Graph.java`      | Parallel arrays: switches, links, costs, up/down |
| **Algorithms** | Merge Sort                 | Edge cost sorting (Kruskal)                      |
|                | Union-Find                 | MST building                                     |
|                | BFS / Dijkstra O(n²)       | Path finding for ping + ASCII path view          |

> Core modules intentionally **avoid `java.util` collections** to highlight DSA implementation.

---

## ⚙️ Build & Run

### Option A — Plain Java (recommended)

```bash
# From repo root
javac -encoding UTF-8 -d bin \
  src/CLI/*.java \
  src/CLI/*/*.java \
  src/Commands/*/*.java \
  src/Engine/*.java \
  src/PDU/*.java \
  src/Topology/*.java \
  src/Utils/*.java \
  src/MAIN_App.java

java -cp bin MAIN_App
```

### Option B — VS Code

* Use the provided `.vscode/launch.json` and `settings.json`.
* Ensure file encoding is **UTF-8** (for box-drawing in ASCII UI).

### Option C — Termux / Linux

```bash
pkg install openjdk-17
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin MAIN_App
```

---

## 🖥️ Quick Start (CLI)

```text
ios> help
ios> add-switch S1
ios> add-switch S2
ios> add-link S1:1 S2:1 10
ios> set-priority S1 4096
ios> show topology
ios> show ascii
ios> show ascii mst
ios> ping S1 S2 3 32
```

Tips:

* `configure terminal` / `end` toggles config mode (prompt changes).
* `show ascii path S1 S3` highlights shortest path with `#`.
* Use larger canvas if needed:

  ```java
  Utils.AsciiTopo.setCanvas(120, 36);
  ```

---

## 🧪 Features (DSA mapping)

| Feature               | DSA / Algo                                               |
| --------------------- | -------------------------------------------------------- |
| CLI command registry  | array registry + manual parser                           |
| Switch/Link store     | parallel arrays (names, priorities, ports, costs, state) |
| `show ascii` renderer | Bresenham lines, adaptive circle layout                  |
| STP-like MST          | Merge Sort + Union-Find (Kruskal local)                  |
| `ping` simulator      | custom PriorityQueue (event loop), BFS/Dijkstra path     |
| History               | custom Stack (future undo/redo)                          |

---

## 📦 Project Tree

```
NetTracer
│   .gitignore
│   LICENSE
│   README.md
│
├── .vscode
│   ├── launch.json
│   └── settings.json
│
├── bin
│   ├── CLI
│   ├── Commands
│   │   ├── General
│   │   └── Switches
│   ├── Engine
│   ├── PDU
│   ├── Topology
│   └── Utils
├── img
└── src
    │   MAIN_App.java
    │
    ├── CLI
    │   ├── CLI_Command.java
    │   ├── CLI_Registry.java
    │   ├── CLI_Shell.java
    │   ├── HistoryManager.java
    │   ├── Stack.java
    │   └── StackNode.java
    │
    ├── Commands
    │   ├── General
    │   │   ├── CMD_Help.java
    │   │   ├── CMD_Hostname.java
    │   │   ├── CMD_Ping.java
    │   │   ├── CMD_Redo.java
    │   │   ├── CMD_Show.java
    │   │   └── CMD_Undo.java
    │   └── Switches
    │       ├── CMD_AddLink.java
    │       ├── CMD_AddSwitch.java
    │       ├── CMD_DelLink.java
    │       ├── CMD_DelSwitch.java
    │       ├── CMD_FailLink.java
    │       ├── CMD_FindSwitch.java
    │       ├── CMD_RecoverLink.java
    │       ├── CMD_RenameSwitch.java
    │       └── CMD_SetPriority.java
    │
    ├── Engine
    │   ├── NetEngine.java
    │   └── NetRouting.java
    │
    ├── PDU
    │   ├── PDU.java
    │   ├── PQNode.java
    │   └── PriorityQueue.java
    │
    ├── Topology
    │   ├── Edge.java
    │   ├── Graph.java
    │   ├── LinkedList.java
    │   └── LLNode.java
    │
    └── Utils
        └── AsciiTopo.java
```

---

## 🧑‍💻 Contributors (Group 04)

* I Wayan Girindra Prasasta ([indraprasasta](https://github.com/indraprasasta)) — F1D02410009
* Fauzan Hari Ramdani ([fauzanhari](https://github.com/fauzanhari)) — F1D02410047
* I Nyoman Widiyasa Jayananda ([Schryzon](https://github.com/Schryzon)) — F1D02410053
* Indira Ramadhani Sabrina ([raraww20](https://github.com/raraww20)) — F1D02410057
* Lalu Tirta Putra Tandela ([putraturnerr](https://github.com/putraturnerr)) — F1D02410119
* Salsabila Nailafahdi ([naiaero](https://github.com/naiaero)) — F1D02410135
* Diaeddin R. M. Abumattar ([hosny27](https://github.com/hosny27)) — F1D02411007

---

## 📌 Current Status

* ✅ Prototype CLI, Graph, STP (MST), ASCII Renderer, and Ping Event Engine are working.
* ✅ Approved by assistant YNK.
* 🔧 Open for improvements (undo/redo wiring, VLAN views, STP states per-port).

---

## 📝 Notes

* Save files as **UTF-8** to ensure box-drawing characters render correctly.
* The simulator is **educational**; timing is *tick-based* (see `NetEngine.tick()` delay).
* For larger topologies, increase canvas via `AsciiTopo.setCanvas(w, h)`.

---

<p align="center"><sub>Made with ♥  for learning real networking with real data structures.</sub></p>
