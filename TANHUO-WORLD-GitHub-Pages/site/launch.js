const game = document.querySelector("#game");
const welcome = document.querySelector("#welcome");
const start = document.querySelector("#start");
const status = document.querySelector("#status");

function loadRuntime() {
  if (typeof window.cheerpjInit === "function") return Promise.resolve();
  return new Promise((resolve, reject) => {
    const loader = document.createElement("script");
    loader.src = "https://cjrtnc.leaningtech.com/5.0/loader.js";
    loader.onload = resolve;
    loader.onerror = () => {
      loader.remove();
      reject(new Error("The game runtime could not be downloaded."));
    };
    document.head.append(loader);
  });
}

start.addEventListener("click", async () => {
  if (window.location.protocol === "file:") {
    status.textContent = "Open this game from its GitHub Pages address.";
    return;
  }
  start.disabled = true;
  status.textContent = "Loading the game. The first start can take a little longer.";
  try {
    await loadRuntime();
    await window.cheerpjInit({ version: 17, status: "splash" });
    window.cheerpjCreateDisplay(-1, -1, game);
    const jar = new URL("./tanhuo-world.jar", window.location.href);
    const width = String(game.clientWidth);
    const height = String(game.clientHeight);
    welcome.hidden = true;
    const exit = await window.cheerpjRunMain("tanhuo.WebMain", "/app" + jar.pathname, width, height);
    if (typeof exit === "number" && exit !== 0) {
      throw new Error("The game could not start (exit " + exit + ").");
    }
  } catch (error) {
    console.error(error);
    welcome.hidden = false;
    status.textContent = "Could not start the game. Check your connection and reload this page.";
  }
});
