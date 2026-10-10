# 🧳 Wanderlust Souvenirs — Online Souvenir Shop (Java Spring Boot)

A complete English-language e-commerce website for selling **souvenirs from around the world** (including Iran), with a full admin panel. Built with **Java + Spring Boot**. Everything is in this project — just run it.

---

## ✨ Features

- Beautiful home page with "Featured souvenirs" and destinations
- Shop with **search**, **filter by destination**, and **sorting** (newest / cheapest / most expensive)
- Product detail pages + related products
- **Shopping cart** and **checkout**
- **Contact** page (messages are saved in the admin panel)
- **About** page
- **Full admin panel**:
  - Dashboard with stats
  - Add / edit / delete products (upload an image or paste an image URL)
  - Manage destinations (categories)
  - View and manage orders (update status)
  - Read and delete messages
  - Shop settings (name, currency, contact info, social links, etc.) and password change
- Built-in database (H2) — **data is saved between runs**, no database to install.

---

## 🖥️ Requirements

- **Java 17** (or newer) — https://adoptium.net/

Check it's installed:
```bash
java -version
```

---

## 🚀 Run in VS Code (easiest)

1. Install these VS Code extensions: **Extension Pack for Java** and **Spring Boot Extension Pack**.
2. `File → Open Folder` and open this `giftshop` folder.
3. Wait 1–3 minutes the first time while libraries download.
4. Open `src/main/java/com/giftshop/GiftshopApplication.java` and click **Run**.
5. When you see `Started GiftshopApplication`, open **http://localhost:8080** 🎉

## 🧑‍💻 Run from the command line (optional)

If Maven is installed:
```bash
mvn spring-boot:run
```

---

## 🔑 Admin panel

- Address: **http://localhost:8080/admin**
- Default password: **`admin123`**  (change it in Settings → Change password)

---

## 🌍 Deploying online (Render)

This project includes a `Dockerfile` so it can be deployed to hosts like Render.

1. Push the project to GitHub (GitHub Desktop keeps the folder structure intact).
2. On render.com: **New + → Web Service**, connect the repo. Render auto-detects the Dockerfile.
3. Pick the Free instance type and create the service. You'll get a public link.

Note: on free tiers the built-in H2 database and uploaded images reset when the app restarts.
For a permanent shop, switch to a managed PostgreSQL database and object storage for images.

---

## 📁 Technical notes

- **Database files** are created automatically in `data/`. Delete that folder to start fresh.
- **Uploaded images** are stored in `uploads/`.
- **Port** defaults to `8080` locally, or the hosting platform's `PORT`.
- **Currency** is configurable in Settings (default `$`).
- **H2 console** (advanced): http://localhost:8080/h2-console — URL `jdbc:h2:file:./data/giftshopdb`, user `sa`, no password.
