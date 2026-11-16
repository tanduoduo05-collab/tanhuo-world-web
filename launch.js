const game = document.querySelector("#game");
const welcome = document.querySelector("#welcome");
const start = document.querySelector("#start");
const status = document.querySelector("#status");
const language = document.querySelector("#language");

const ui = {
  en: {
    title: "Tanhuo World", intro: "Grow your garden. Explore the Wildflower Trail.", play: "Play game",
    controls: "Use the touch buttons or the arrow keys.", rotate: "Rotate to play",
    rotateHelp: "Turn your phone sideways. If the screen stays upright, turn off rotation lock.",
    local: "Open this game from its GitHub Pages address.",
    load: "Loading the game. The first start can take a little longer.",
    error: "Could not start the game. Check your connection and reload this page."
  },
  zh: {
    title: "TANHUO WORLD", intro: "种菜、探索野花小径，在篝火旁一起看电影。", play: "开始游戏",
    controls: "使用触屏按钮或键盘方向键。", rotate: "请横屏游玩",
    rotateHelp: "请横着拿手机。如果画面没有旋转，请关闭竖屏锁定。",
    local: "请从游戏的 GitHub Pages 网址打开。",
    load: "正在加载游戏，首次启动可能稍慢。",
    error: "游戏没有启动。请检查网络后刷新重试。"
  }
};

function localizeWelcome() {
  const copy = ui[language.value];
  document.documentElement.lang = language.value === "zh" ? "zh-Hans" : "en";
  document.querySelector("#title").textContent = copy.title;
  document.querySelector("#intro").textContent = copy.intro;
  document.querySelector("#rotate-title").textContent = copy.rotate;
  document.querySelector("#rotate-help").textContent = copy.rotateHelp;
  start.textContent = copy.play;
  status.textContent = copy.controls;
}
language.addEventListener("change", localizeWelcome);

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
  const chosenLanguage = language.value;
  const copy = ui[chosenLanguage];
  if (window.location.protocol === "file:") {
    status.textContent = copy.local;
    return;
  }
  start.disabled = true;
  language.disabled = true;
  status.textContent = copy.load;
  try {
    await loadRuntime();
    await window.cheerpjInit({ version: 17, status: "splash", enableInputMethods: true });
    window.cheerpjCreateDisplay(-1, -1, game);
    const jar = new URL("./tanhuo-world-cinema-titles.jar", window.location.href);
    const width = String(game.clientWidth);
    const height = String(game.clientHeight);
    welcome.hidden = true;
    const exit = await window.cheerpjRunMain("tanhuo.WebMain", "/app" + jar.pathname, width, height, chosenLanguage);
    if (typeof exit === "number" && exit !== 0) {
      throw new Error("The game could not start (exit " + exit + ").");
    }
  } catch (error) {
    console.error(error);
    welcome.hidden = false;
    status.textContent = copy.error;
  }
});
