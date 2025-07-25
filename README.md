![QuranWhispers Demo](opening.gif)
---

**QuranWhispers** is an open-source, non-commercial platform designed as a spiritual companion to the Quran, offering users an emotional and interactive experience. Built with a modern Java stack, it features a frontend powered by JavaFX and a robust backend with database support. The platform allows users to explore Quranic verses by emotion or theme, listen to soothing recitations, and engage in a smart forum. It offers personalized verse searches, daily recitations and duas, social sharing, and an intuitive admin control panel. Through global discussions in the forum, QuranWhispers deepens the user's connection with the Quran, ensuring a holistic and meaningful spiritual journey.


<br>
<br>

# 🌟 Features

### 📖 Emotional & Thematic Quran Search
- Search and discover Quranic verses by emotion (e.g., hope, patience) or theme (e.g., justice, family).
- AI-powered verse suggestions based on user input.
- Dynamically generate and download beautiful posters for any verse.
- Share verses with friends.

### 🎧 Recitations
- Listen to high-quality Quranic recitations.
- Upload recitations of your favourite reciters.

### 🤲 Daily Duas
- Access a curated list of daily duas with translations.

### 💬 Interactive Forum
- Participate in a real-time forum with smart commands (e.g., `/question`, `/verseEmotion`, `/send`).
- Admins can moderate, delete, or manage messages and users.

### 👤 User Profiles
- Save favorite verses.
- View received verses and statistics.

### 🔔 Notifications & Alerts
- Receive updates and notifications within the app.
- Customized and detailed alert toasters

### 🛡️ Admin Controls
- Approve or reject recitations.
- Add new verses and duas
- manage users, and moderate forum activity.
- access the database tables of users, verse, dua, recitations



<br>
<br>

# 🛠️ Tech Stack

- **Frontend:** JavaFX, FXML, SCSS
- **Backend:** Java, H2 Database, Google Gemini AI API
- **Networking:** Java Sockets
- **Build Tools:** Maven

<br>
<br>

# 📁 Project Structure

```
QuranWhispers/
│
├── QuranWhispers_Backend/                      # Backend server
│   ├── src/main/java/  
│   └── data/                                   # H2 database files
│
├── QuranWhispers_Frontend/                     # JavaFX GUI frontend
│   ├── src/main/java/
│   │   ├── controller/                         
│   │   ├── quranwhispers_frontend/MainApp.java # executable                                 
│   │   ├── shared/                             # shared object
│   │   └── util/                               # utility class
│   │
│   └── src/main/resources/
│       ├── data/recitations_audio              # mp3 recitations
│       ├── fxml/                               # markup files
│       ├── styles/                             # SCSS stylesheets
│       ├── images/            
│       ├── video/  
│       └── sound/      
│
├── README.md
└── LICENSE
```

<br>
<br>

# 🚀 Installation & Usage

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Internet connection (for AI and Quran API features)

### 1. Clone the Repository

```sh
git clone https://github.com/ahammadshawki8/QuranWhispers.git
cd QuranWhispers
```

### 2. Build the Backend

```sh
cd QuranWhispers_Backend
mvn clean package
```

### 3. Build the Frontend

```sh
cd ../QuranWhispers_Frontend
mvn clean package
```

### 4. Run the Backend Server

```sh
cd ../QuranWhispers_Backend
mvn exec:java -Dexec.mainClass="com.example.server.HelloApplication"
```

### 5. Run the Frontend Application

```sh
cd ../QuranWhispers_Frontend
mvn exec:java -Dexec.mainClass="quranwhispers_frontend.MainApp"
```

<br>
<br>

# 🧑‍💻 Contribution Guidelines

We welcome contributions! To get started:

- Fork this repository: [https://github.com/ahammadshawki8/QuranWhispers/fork](https://github.com/ahammadshawki8/QuranWhispers/fork)

- Create a new branch for your feature or fix:
  ```bash
  git checkout -b feature/YourFeature
  ```

- Commit your changes with a clear message:
  ```bash
  git commit -m "Add YourFeature"
  ```

- Push your branch to your forked repository:
  ```bash
  git push origin feature/YourFeature
  ```

- Open a pull request (PR)  and describe your changes: [https://github.com/ahammadshawki8/QuranWhispers/pulls](https://github.com/ahammadshawki8/QuranWhispers/pulls)

- Found a bug or have a feature request? Create an issue here: [https://github.com/ahammadshawki8/QuranWhispers/issues](https://github.com/ahammadshawki8/QuranWhispers/issues)

<br>

**Important:**  
- ✅ Please ensure your code adheres to the project's coding standards.  
- ✅ Make sure all tests pass before submitting your PR.
- ✅ Before contributing, please read the [LICENSE](LICENSE) and ensure your contributions are non-commercial..

We appreciate your help to make this project better! 🚀

<br>
<br>

# 📜 License
🚫 **IMPORTANT NOTICE: NO COMMERCIAL USE** 🚫

This project is licensed under the [PolyForm Noncommercial License 1.0.0](LICENSE).

- ✅ You are welcome to use it for learning, personal projects, and non-commercial collaboration.  
- ❌ You **may not** use this project (or any modified versions) for commercial gain, competitions, or any monetary benefit **without explicit written permission**.

Any violations will be considered **copyright infringement** and may lead to legal action.

For questions or commercial licensing, please contact [Ahammad Shawki](https://ahammadshawki8.github.io/).

<br>
<br>

# 📬 Contact

- **FrontEnd Lead:** [Ahammad Shawki](https://ahammadshawki8.github.io/)
- **BackEnd Lead:** [S.M. Abu Fayeem](https://www.facebook.com/abu.fayeem)

<br>
<br>

# 🙏 Acknowledgements
- Project Supervisor: [Ishrat Jahan](https://cse.buet.ac.bd/faculty/faculty_detail/ishrat)
- [Fawaz Ahmed Quran API](https://github.com/fawazahmed0/quran-api)
- [Google Gemini AI](https://ai.google.dev/)
- All contributors and the open-source community

---
<br>

*QuranWhispers – Bringing the Quran closer to your heart, one emotion at a time.*