# Online Examination and Grading System

A JavaFX desktop application for administering multiple-choice, true/false,
and short-answer exams, and automatically grading them.

## How to Run

The project can be built either with **Maven** (recommended — it downloads
JavaFX for you) or with the **plain JDK** if you already have a JavaFX SDK.

### Option A: Maven (recommended)

**Prerequisites:** JDK 17+ and Maven, both with internet access to Maven
Central (the `pom.xml` downloads the correct JavaFX jars automatically —
you do not need to install a separate JavaFX SDK).

```bash
# From the project root (the folder containing pom.xml)
mvn clean javafx:run
```

To build a runnable jar instead:
```bash
mvn clean package
java -jar target/online-examination-system.jar
```
(Running the packaged jar still requires a JavaFX-enabled JDK on the
machine, since a fat jar does not bundle native JavaFX binaries.)

**Using an IDE with Maven (IntelliJ IDEA / Eclipse):**
1. Open the project folder — the IDE will detect `pom.xml` and import it as
   a Maven project automatically.
2. Let it download dependencies.
3. Run the `javafx:run` Maven goal, or run `com.exam.ui.MainApp` directly
   after adding the VM options shown in Option B below.

**Using NetBeans specifically:** NetBeans's default green **Run** button
binds to the `exec-maven-plugin` (`exec:exec` goal) for plain Maven jar
projects, which runs `java -classpath ...` *without* the JavaFX module
path — so clicking Run will fail with:
```
Error: JavaFX runtime components are missing, and are required to run this application
```
This project ships an `nbactions.xml` file (next to `pom.xml`) that tells
NetBeans to use the `javafx:run` goal instead, which sets up the module
path correctly. NetBeans picks this up automatically as long as
`nbactions.xml` was imported along with the rest of the project. If Run
still fails, go to **Project Properties → Actions → Run project** and set
"Execute Goals" to `javafx:run` manually — or just open a terminal in
NetBeans (right-click the project → **Open in Terminal**) and run
`mvn javafx:run` directly.

If the build output shows
`skip non existing resourceDirectory ...\src\main\resources`, the
`src/main/resources/styles.css` file didn't make it into your project
(this happens if only `src/main/java` was copied in during import).
Confirm this path exists relative to `pom.xml`:
```
src/main/resources/styles.css
```
and if it's missing, copy the `resources` folder back in next to `java`.

### Option B: Plain JDK + a manually installed JavaFX SDK

**Prerequisites:**
- JDK 17 or later
- JavaFX SDK 17+ (matching your JDK version) — download from
  [https://openjfx.io](https://openjfx.io) if it isn't already installed.
  On Ubuntu/Debian you can alternatively run: `sudo apt install openjfx`

**Compile:**
```bash
# From the project root
mkdir out
find src/main/java -name "*.java" > sources.txt
javac -d out --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls @sources.txt
cp src/main/resources/styles.css out/styles.css
```

**Run:**
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls -cp out com.exam.ui.MainApp
```

Replace `/path/to/javafx-sdk/lib` with the `lib` folder of your JavaFX SDK
(e.g. `/usr/share/openjfx/lib` on Ubuntu after `apt install openjfx`).

**Using an IDE without Maven:**
1. Import as a plain Java project with `src/main/java` as the source root.
2. Add the JavaFX SDK jars to the module path.
3. Set VM options: `--module-path <path-to-javafx-lib> --add-modules javafx.controls`
4. Run `com.exam.ui.MainApp`.

## What the Application Does

1. **Welcome / Registration screen** – the candidate enters their name,
   student ID, chooses an exam duration, and can optionally filter the
   exam to a single question category. Input is validated before the
   exam can start.
2. **Admin Login screen** – the "Manage Questions" button on the Welcome
   screen no longer opens the question bank directly; it first requires
   signing in (demo credentials: `admin` / `admin123`). Wrong credentials
   show an error and, after 3 failed attempts, return to the Welcome
   screen. This keeps question-bank editing separate from the student
   flow.
3. **Question Bank Management screen** *(admin-only)* – view all questions
   currently in the bank (pre-loaded with 10 sample questions, each
   tagged with a category), filter the table by category, add new
   Multiple Choice, True/False, or Short Answer questions with a category
   of your choosing, or remove existing ones.
4. **Exam screen** – one question at a time, with a live countdown timer,
   a progress bar, and Previous/Next navigation that remembers answers
   already given. The exam auto-submits when time runs out.
5. **Results screen** – marks obtained, percentage, letter grade, a full
   question-by-question review table, and an **Export Results to File**
   button that saves a formatted text report of the attempt to disk.

## Project Structure

```
pom.xml                                 Maven build file
nbactions.xml                           NetBeans run/debug action mapping
src/main/resources/
└── styles.css                          Application stylesheet
src/main/java/
└── com/exam/
    ├── model/
    │   ├── Question.java                Abstract base class (abstraction)
    │   ├── MultipleChoiceQuestion.java   Concrete subtype
    │   ├── TrueFalseQuestion.java        Concrete subtype
    │   ├── ShortAnswerQuestion.java      Concrete subtype
    │   ├── Student.java                  Encapsulated candidate data
    │   ├── Exam.java                     Holds a List<Question>
    │   ├── AnswerRecord.java             One graded answer
    │   ├── ExamResult.java               Full grading outcome
    │   └── Grade.java                    Letter-grade enum
    ├── exception/
    │   ├── InvalidAnswerException.java
    │   └── ExamConfigurationException.java
    ├── service/
    │   ├── ExamService.java              Question bank + grading logic
    │   ├── QuestionBankFactory.java      Builds the starter question set
    │   ├── AdminAuthService.java         Admin credential check
    │   └── ReportExporter.java           Writes a result report to disk
    └── ui/
        ├── MainApp.java                  Application entry / navigation
        ├── WelcomeView.java
        ├── AdminLoginView.java
        ├── QuestionManagerView.java
        ├── ExamView.java
        ├── ResultView.java
        └── UIUtils.java                  Shared alert/button helpers
```

## Where Each Requirement Is Demonstrated

| Requirement | Where |
|---|---|
| **Encapsulation** | All model classes (`Question`, `Student`, `Exam`, …) keep fields `private`/`protected` with validating getters/setters. |
| **Inheritance** | `MultipleChoiceQuestion`, `TrueFalseQuestion`, `ShortAnswerQuestion` all extend the abstract `Question` class. |
| **Polymorphism** | `Question.isCorrect()`, `getCorrectAnswerDisplay()`, and `getQuestionType()` are overridden differently by each subclass; `ExamService.gradeExam()` calls `question.isCorrect(...)` polymorphically without knowing the concrete type. |
| **Abstraction** | `Question` is an abstract class exposing only the operations client code needs, hiding how each question type actually grades itself. |
| **Operators & expressions** | Percentage/grade calculations in `ExamResult`/`Grade`, timer countdown arithmetic in `ExamView`. |
| **Control structures** | Loops over questions/options throughout `service` and `ui` packages; `switch` expression in `QuestionManagerView.handleAddQuestion()`; conditional grade banding in `Grade.fromPercentage()`. |
| **Methods & modular programming** | Logic is split across `model`, `exception`, `service`, and `ui` packages, each with focused, single-purpose classes/methods. |
| **Exception handling** | Custom checked exceptions `InvalidAnswerException` and `ExamConfigurationException`; `try/catch` in `ExamService.gradeExam()`, `WelcomeView`, `QuestionManagerView`, and `ResultView.handleExport()` to catch and display validation/IO errors without crashing. |
| **Arrays / collections** | `ArrayList` inside `Exam` and `MultipleChoiceQuestion`; `ObservableList` question bank in `ExamService`; `LinkedHashMap`/`Map` for tracking a student's in-progress answers; category filtering built with Java Streams over the question list. |
| **Event-driven programming** | Button `setOnAction` handlers, `ComboBox` selection listeners, `Timeline` for the countdown timer, `Enter`-key submit on the login form. |
| **GUI component handling** | `TableView`, `ComboBox`, `Spinner`, `RadioButton`/`ToggleGroup`, `PasswordField`, `ProgressBar`, `FileChooser`, `Alert` dialogs, etc. |
| **Input validation** | Constructors/setters throw `IllegalArgumentException` on bad data; UI catches these and shows an `Alert` rather than crashing; admin login rejects empty or incorrect credentials. |

## Sample Question Bank

The app ships with 10 sample questions, each tagged with a category
(OOP Concepts, Collections, JavaFX Basics, Exception Handling), so it can
be demonstrated immediately; more can be added — with a category of your
choosing — from the Question Bank Management screen (after signing in).

## Admin Access

The Question Bank Management screen is gated behind a simple login to
keep it separate from the student-facing flow:

- **Username:** `admin`
- **Password:** `admin123`

These are intentionally hard-coded in `AdminAuthService` for demonstration
purposes — a real system would hash and store credentials securely rather
than compare plaintext strings in code.
