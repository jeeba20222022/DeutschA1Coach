# Deutsch A1 – Mein Coach V0.2 — Cloud Build

Ce projet est préparé pour produire un APK Android installable sans installer Android Studio.

## Option recommandée : Codemagic

1. Créer un compte Codemagic.
2. Mettre ce projet dans un dépôt GitHub/GitLab/Bitbucket.
3. Dans Codemagic, choisir **Add application** et connecter le dépôt.
4. Le fichier `codemagic.yaml` à la racine configure le build.
5. Lancer le workflow **Deutsch A1 – Mein Coach V0.2 Cloud APK**.
6. À la fin, télécharger l'artefact `app-debug.apk`.
7. Transférer l'APK sur Android et l'installer.

Cette version produit un **APK debug installable**. Aucun keystore personnel n'est nécessaire pour ce test.

## Option GitHub Actions

Le dossier `.github/workflows/android-apk.yml` permet aussi de compiler dans GitHub :

1. Déposer tout le projet dans un dépôt GitHub.
2. Aller dans **Actions**.
3. Sélectionner **Build Android APK**.
4. Cliquer sur **Run workflow**.
5. Une fois terminé, télécharger l'artefact `DeutschA1Coach-v0.2-apk`.

## Important

Le projet contient actuellement le contenu pédagogique déjà présent dans V0.2. Il s'agit d'un prototype éducatif et non d'une reproduction intégrale du manuel commercial.
