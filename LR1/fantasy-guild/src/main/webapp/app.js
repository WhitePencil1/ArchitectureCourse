const heroForm = document.getElementById("hero-form");
const guildForm = document.getElementById("guild-form");
const membershipForm = document.getElementById("membership-form");

let guilds = [];
let selectedGuildId = null;

function showMessage(text, isError = false) {
    const message = document.getElementById("message");
    message.textContent = text;
    message.className = isError ? "error" : "success";
}

// При ошибке возвращаем null, при ответе 204 — пустой объект
async function request(path, method = "GET", data = null, showResponse = true) {
    for (const button of document.querySelectorAll("button")) {
        button.disabled = true;
    }

    try {
        const options = { method: method };
        if (data !== null) {
            options.headers = { "Content-Type": "application/json" };
            options.body = JSON.stringify(data);
        }

        const response = await fetch("api/" + path, options);
        const text = await response.text();
        let result = {};

        if (text !== "") {
            try {
                result = JSON.parse(text);
            } catch {
                result = text;
            }
        }

        if (showResponse || !response.ok) {
            document.getElementById("request-info").textContent =
                method + " /api/" + path + " — HTTP " + response.status;

            let output = "Без тела ответа";
            if (text !== "") {
                output = typeof result === "string" ? result : JSON.stringify(result, null, 2);
            }
            document.getElementById("response").textContent = output;
        }

        if (!response.ok) {
            let errorMessage = "Ошибка запроса: HTTP " + response.status;
            if (result && result.error) errorMessage = result.error;
            showMessage(errorMessage, true);
            return null;
        }
        return result;
    } catch (error) {
        showMessage("Не удалось отправить запрос. Откройте страницу через GlassFish на порту 8080.", true);
        document.getElementById("request-info").textContent = method + " /api/" + path;
        document.getElementById("response").textContent = error.message;
        return null;
    } finally {
        for (const button of document.querySelectorAll("button")) {
            button.disabled = false;
        }
    }
}

async function loadData(showResponse = true) {
    const loadedGuilds = await request("guilds", "GET", null, showResponse);
    if (loadedGuilds === null) return false;

    const heroes = await request("heroes", "GET", null, showResponse);
    if (heroes === null) return false;

    if (!Array.isArray(loadedGuilds) || !Array.isArray(heroes)) {
        showMessage("Сервер вернул неправильный формат списка.", true);
        return false;
    }

    guilds = loadedGuilds;
    showGuilds();
    showHeroes(heroes);

    if (selectedGuildId !== null) {
        const guild = guilds.find(guild => guild.id === selectedGuildId);
        if (guild) {
            await loadMembers(selectedGuildId, false);
        } else {
            selectedGuildId = null;
            document.getElementById("members").hidden = true;
        }
    }
    return true;
}

function showHeroes(heroes) {
    const table = document.getElementById("heroes");
    table.textContent = "";

    if (heroes.length === 0) {
        const cell = table.insertRow().insertCell();
        cell.colSpan = 6;
        cell.textContent = "Героев пока нет.";
    }

    for (const hero of heroes) {
        const row = table.insertRow();
        row.insertCell().textContent = hero.id;
        row.insertCell().textContent = hero.name;
        row.insertCell().textContent = hero.level;
        row.insertCell().textContent = hero.health;

        let guildName = "Без гильдии";
        if (hero.guildId !== null && hero.guildId !== undefined) {
            const guild = guilds.find(guild => guild.id === hero.guildId);
            guildName = (guild ? guild.name : "Гильдия") + " (#" + hero.guildId + ")";
        }
        row.insertCell().textContent = guildName;

        const actions = row.insertCell();
        actions.innerHTML = '<button type="button">Открыть</button> <button type="button" class="danger">Удалить</button>';
        actions.children[0].onclick = function () { loadHero(hero.id); };
        actions.children[1].onclick = function () { deleteHero(hero.id); };
    }
}

function showGuilds() {
    const table = document.getElementById("guilds");
    table.textContent = "";

    const select = document.getElementById("guild-select");
    const previousId = select.value;
    select.innerHTML = '<option value="">Выберите гильдию</option>';

    if (guilds.length === 0) {
        const cell = table.insertRow().insertCell();
        cell.colSpan = 4;
        cell.textContent = "Гильдий пока нет.";
    }

    for (const guild of guilds) {
        select.add(new Option(guild.name + " (#" + guild.id + ")", guild.id));
        const row = table.insertRow();
        row.insertCell().textContent = guild.id;
        row.insertCell().textContent = guild.name;
        row.insertCell().textContent = guild.description || "—";

        const actions = row.insertCell();
        actions.innerHTML = '<button type="button">Открыть</button> <button type="button">Участники</button> <button type="button" class="danger">Удалить</button>';
        actions.children[0].onclick = function () { loadGuild(guild.id); };
        actions.children[1].onclick = function () { loadMembers(guild.id); };
        actions.children[2].onclick = function () { deleteGuild(guild.id); };
    }

    select.value = previousId;
    if (select.selectedIndex === -1) select.value = "";
}

async function loadHero(id) {
    const hero = await request("heroes/" + id);
    if (hero === null) return;

    heroForm.elements["id"].value = hero.id;
    heroForm.elements["name"].value = hero.name;
    heroForm.elements["level"].value = hero.level;
    heroForm.elements["health"].value = hero.health;
    membershipForm.elements["heroId"].value = hero.id;
    heroForm.elements["name"].focus();
    showMessage("Герой загружен в форму.");
}

async function loadGuild(id) {
    const guild = await request("guilds/" + id);
    if (guild === null) return;

    guildForm.elements["id"].value = guild.id;
    guildForm.elements["name"].value = guild.name;
    guildForm.elements["description"].value = guild.description || "";
    guildForm.elements["name"].focus();
    showMessage("Гильдия загружена в форму.");
}

async function loadMembers(id, showResponse = true) {
    const heroes = await request("guilds/" + id + "/heroes", "GET", null, showResponse);
    if (heroes === null) return;

    if (!Array.isArray(heroes)) {
        showMessage("Сервер вернул неправильный формат списка участников.", true);
        return;
    }

    selectedGuildId = id;
    document.getElementById("members").hidden = false;
    document.getElementById("members-heading").textContent = "Участники гильдии #" + id;
    const table = document.getElementById("guild-members");
    table.textContent = "";

    if (heroes.length === 0) {
        const cell = table.insertRow().insertCell();
        cell.colSpan = 4;
        cell.textContent = "У гильдии пока нет героев.";
    }

    for (const hero of heroes) {
        const row = table.insertRow();
        row.insertCell().textContent = hero.id;
        row.insertCell().textContent = hero.name;
        row.insertCell().textContent = hero.level;
        row.insertCell().textContent = hero.health;
    }
    if (showResponse) showMessage("Участники загружены.");
}

heroForm.onsubmit = async function (event) {
    event.preventDefault();
    const id = heroForm.elements["id"].value;
    const hero = {
        name: heroForm.elements["name"].value.trim(),
        level: Number(heroForm.elements["level"].value),
        health: Number(heroForm.elements["health"].value)
    };

    let result;
    if (id === "") {
        result = await request("heroes", "POST", hero);
    } else {
        result = await request("heroes/" + id, "PUT", hero);
    }
    if (result === null) return;

    heroForm.reset();
    showMessage("Герой сохранён.");
    await loadData(false);
};

guildForm.onsubmit = async function (event) {
    event.preventDefault();
    const id = guildForm.elements["id"].value;
    const guild = {
        name: guildForm.elements["name"].value.trim(),
        description: guildForm.elements["description"].value.trim() || null
    };

    let result;
    if (id === "") {
        result = await request("guilds", "POST", guild);
    } else {
        result = await request("guilds/" + id, "PUT", guild);
    }
    if (result === null) return;

    guildForm.reset();
    showMessage("Гильдия сохранена.");
    await loadData(false);
};

async function deleteHero(id) {
    if (!confirm("Удалить героя #" + id + "?")) return;
    const result = await request("heroes/" + id, "DELETE");
    if (result === null) return;

    if (heroForm.elements["id"].value === String(id)) heroForm.reset();
    showMessage("Герой удалён.");
    await loadData(false);
}

async function deleteGuild(id) {
    if (!confirm("Удалить гильдию #" + id + "? Герои останутся без гильдии.")) return;
    const result = await request("guilds/" + id, "DELETE");
    if (result === null) return;

    if (guildForm.elements["id"].value === String(id)) guildForm.reset();
    showMessage("Гильдия удалена.");
    await loadData(false);
}

const heroLookup = document.getElementById("hero-lookup");
heroLookup.onsubmit = function (event) {
    event.preventDefault();
    loadHero(heroLookup.elements["id"].value);
};

const guildLookup = document.getElementById("guild-lookup");
guildLookup.onsubmit = function (event) {
    event.preventDefault();
    loadGuild(guildLookup.elements["id"].value);
};

membershipForm.onsubmit = async function (event) {
    event.preventDefault();
    const heroId = membershipForm.elements["heroId"].value;
    const guildId = membershipForm.elements["guildId"].value;
    if (guildId === "") {
        showMessage("Выберите гильдию.", true);
        return;
    }

    const result = await request("heroes/" + heroId + "/guild/" + guildId, "PUT");
    if (result === null) return;
    showMessage("Гильдия героя изменена.");
    await loadData(false);
};

document.getElementById("leave-guild").onclick = async function () {
    const input = membershipForm.elements["heroId"];
    if (!input.reportValidity()) return;

    const result = await request("heroes/" + input.value + "/guild", "DELETE");
    if (result === null) return;
    showMessage("Герой вышел из гильдии.");
    await loadData(false);
};

document.getElementById("refresh").onclick = async function () {
    showMessage("Обновление списков…");
    if (await loadData()) showMessage("Списки обновлены.");
};

async function start() {
    if (await loadData()) showMessage("Данные загружены.");
}

start();
