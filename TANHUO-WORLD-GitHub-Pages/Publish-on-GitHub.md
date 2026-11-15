# Publish Tanhuo World with GitHub Pages

1. On GitHub, create a **Public** repository named `tanhuo-world-web`.
   Check **Add a README file** to create the `main` branch.
2. Open the repository's **Code** tab and select **Add file → Upload files**.
   Upload the contents of the extracted `site` folder to the repository root.
   In particular, `index.html` and `tanhuo-world.jar` belong at the root.
   Click **Commit changes**. If the upload picker hides `.nojekyll`, create
   a file with that name at the root using **Add file → Create new file**.
   It can be empty or contain a short comment.
3. In **Settings → Pages → Build and deployment**, select **Deploy from a
   branch**, choose **main / (root)**, and click **Save**.
4. When the deployment completes, use **Visit site**. Share that URL.
   Players can use the arrow keys or the on-screen buttons. On a phone,
   turn it sideways before pressing **Play game**.

GitHub's official guide:
https://docs.github.com/en/pages/getting-started-with-github-pages/configuring-a-publishing-source-for-your-github-pages-site

The web game is built with CheerpJ Community Edition and loads the Java
runtime from its CDN. The first launch needs an internet connection and can
take longer than subsequent launches. No Java installation or browser extension
is needed for players. Use the published HTTPS link, since the browser game
requires HTTP hosting with range requests.

## Update the game

Keep this extracted folder as your browser-version project. Edit the Java files
in `src`. From PowerShell in this folder, run:

```powershell
powershell -ExecutionPolicy Bypass -File .\build-web.ps1
```

Replace `tanhuo-world.jar` in the GitHub repository with the newly built
`site\tanhuo-world.jar` and commit it to `main`. GitHub Pages republishes at the
same address. The normal desktop entry point is still `tanhuo.Main`, and the
browser entry point is `tanhuo.WebMain`.
