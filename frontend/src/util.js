import { ref } from 'vue';

export function getURL(postfix) {
    if (import.meta.env.DEV) {
        return "http://localhost:8080/m1/" + postfix;
        return "https://tg.dondevelops.de/2/" + postfix;
    }
    return "./" + postfix;
}

export function circlesProgress(current, max) {
    let res = "⬤".repeat(current);
    while(res.length < max) {
        res += "◯";
    }
    return res;
}

export let selectedRow = ref(-1);
