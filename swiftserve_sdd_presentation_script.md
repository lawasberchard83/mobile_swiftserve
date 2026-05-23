# SwiftServe: SDD-Aligned Video Presentation Script

This guide is updated to reflect your **System Design Document (SDD)**. It covers the full ecosystem (Spring Boot backend, React web dashboard, and Android mobile app) while strictly adhering to the rubric's requirement to explain the **Vertical Slicing Approach** and **MVP Design Pattern** in your Android source code.

---

## ⏱️ 10-Minute Timeline Breakdown

| Section | Content Focus | Target Time | Rubric Points |
| :--- | :--- | :--- | :--- |
| **1. Intro & Problem** | Campus food queues, student constraints, project overview. | 1.5 mins | 15 Points |
| **2. Features & Tech** | Student App + Vendor Dashboard, Tech Stack (Spring Boot, React, Android). | 1.5 mins | 20 Points |
| **3. Live Walkthrough** | Ordering food on Android, managing orders on the React Dashboard. | 3.5 mins | 20 Points |
| **4. Source Code** | Vertical Slicing & MVP Pattern (Android architecture). | 3.0 mins | 20 Points |
| **5. Conclusion** | Wrap-up and submission checklist reminder. | 0.5 mins | 25 Points |

---

## 🎬 Spoken Script & Slide Outline

### Slide 1: Title & Introduction
* **Visual:** Slide with Project Title: "SWIFTSERVE - Campus Logistics & Food Delivery", your name (Berchard Lawrence D. Lawas), and IT342-G8.
* **Script:**
> "Hello, everyone. My name is Berchard Lawrence Lawas, and today I am excited to present **SwiftServe**, my final project for System Integration and Architecture. SwiftServe is a specialized campus delivery platform designed to connect university students and faculty with on-campus food stalls and nearby vendors."

---

### Slide 2: Problem Statement & Objectives
* **Visual:** Bullet points: Long queues, skipped meals, lack of digital presence for stalls, landmark delivery.
* **Script:**
> "The core problem SwiftServe solves is the time constraint students face between classes. Long queues at campus stalls often lead to skipped meals or tardiness. 
>
> SwiftServe solves this by providing a unified ecosystem: an Android app for students to pre-order food, and a React web dashboard for campus vendors to manage their menus and incoming orders. A key innovation in our system is 'landmark-based' delivery, tailored specifically to the layout of our university, ensuring food reaches specific buildings and rooms accurately."

---

### Slide 3: Key Features & Technologies Used
* **Visual:** Architecture Diagram showing Web Client (React), Mobile Client (Android), Backend API (Spring Boot), Database (PostgreSQL).
* **Script:**
> "Our system's core features include secure JWT user authentication, a live product catalog, cart management, and a robust checkout process capturing campus drop-off points.
>
> To achieve this, I implemented a full-stack architecture:
> * **Backend:** Java 17 with Spring Boot 3 and Spring Security, connected to a **PostgreSQL** relational database handling users, products, carts, and orders.
> * **Vendor Interface:** A modern web dashboard built with React, TypeScript, and Tailwind CSS.
> * **Student Interface:** A native Android application built with Kotlin, utilizing Retrofit for high-performance API communication."

---

### Part 4: System Walkthrough (Live Demo)
* **Action:** Switch to screen recording showing the Android Emulator side-by-side with the React Web Dashboard (if available), or just the Android app.
* **Script:**
> *"Let’s walk through the core user journeys. We'll start with the Student Food Order journey on the Android app.*
>
> *[Action: Show Android app login and dashboard]*
> *Here, a student logs into SwiftServe securely. They are greeted by the dashboard displaying stalls currently active on campus. I can search for a specific meal or filter by categories.*
> 
> *[Action: Add item to cart and proceed to checkout]*
> *I’ll add a meal to my cart and proceed to checkout. During checkout, the system asks for specific campus delivery information—like the Building Name and Room Number. Once I place the order, it is saved securely to our PostgreSQL database.*
> 
> *[Action: Switch to Web Dashboard or explain the vendor side]*
> *Simultaneously, on the Vendor Web Dashboard, the food stall owner receives this order in real-time. From their panel, they can confirm the order, update 'Out of Stock' items, and manage their daily menu, ensuring a seamless flow of information between the stall and the student."*

---

### Part 5: Source Code Presentation (Vertical Slicing & MVP)
* **Action:** Switch screen to Android Studio. Expand the project tree showing `com.swiftserve.app.feature`.

#### 1. Vertical Slicing Approach
* **Script:**
> "Now, let’s dive into the Android source code. To ensure our mobile app is scalable and maintainable, I implemented a **Vertical Slicing Approach**. 
> 
> Rather than organizing files by technical type—like putting all UI screens in one folder and all business logic in another—I organized the codebase by **feature**. 
> 
> *[Action: Point to the `feature` directory in the IDE]*
> As you can see, inside the `feature` package, we have separate modules for `auth`, `dashboard`, `checkout`, `payment`, and `profile`. Each feature module contains everything it needs to function: its UI layouts, data adapters, logic contracts, and presenters. This means if I need to update the checkout process, I only work within the `checkout` slice without affecting the rest of the app. The flow for each feature goes strictly from UI, to Logic, to Data."

#### 2. MVP Design Pattern
* **Action:** Open `LoginContract.kt`, `LoginActivity.kt`, and `LoginPresenter.kt` as examples.
* **Script:**
> "Within these vertical slices, we utilize the **Model-View-Presenter (MVP)** design pattern. Let's look at the Authentication slice as an example.
> 
> *[Action: Show LoginContract.kt]*
> First, we define a **Contract**. The Contract contains two interfaces: The View and the Presenter. This establishes a strict set of rules for how the UI and logic communicate.
> 
> *[Action: Show LoginActivity.kt]*
> The **View** is implemented by the Activity. Its only job is to handle UI elements—like showing a loading spinner or capturing the user's email and password. It contains absolutely zero business logic or API calls.
> 
> *[Action: Show LoginPresenter.kt]*
> The **Presenter** acts as the middleman. When the user clicks 'Login', the View tells the Presenter. The Presenter then communicates with the **Model**—our data layer utilizing Retrofit to hit our Spring Boot API. 
> 
> *[Action: Highlight the callback in the presenter]*
> Once the backend responds with a success or error, the Presenter decides what happens next and commands the View to either `navigateToDashboard()` or `showError()`. This strict separation guarantees clean structure, prevents UI memory leaks, and makes the logic highly testable."

---

### Slide 6: Conclusion
* **Visual:** Summary Slide (Spring Boot + React + Android MVP = Seamless Campus Logistics).
* **Script:**
> "To conclude, SwiftServe integrates a powerful Spring Boot backend with a React web interface and an efficiently architected Android MVP application. Together, they create a robust solution that solves real campus logistical problems. Thank you for your time, and I am ready for any questions."

---

## ⚠️ Final Submission Reminders
1. **Source Code Link (10 points):** Ensure your Google Drive link to the source code is set to **"Anyone with the link can view"**.
2. **Video Link:** Same for the video repository. Test the link in an Incognito window to be 100% sure. Do not upload actual files to the submission portal.
3. **Time Limit:** Practice reading this script with your screen recording to ensure you stay under the strict **10-minute maximum**. The script above takes roughly 6-8 minutes to speak at a normal pace, giving you plenty of buffer time for your live demo actions.
