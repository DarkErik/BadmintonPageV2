<template>
    <p class="text-6xl text-center font-bold w-full text-gray-800 px-4">Spielübersicht</p>
    <p class="text-3xl text-center font-bold w-full text-gray-600 pb-10">{{teamname}}</p>

    <div class="p-5">
        <div class="flex flex-col gap-10">
            <MatchTableRow v-for="(game, index) in store.games" :key="index" :gameIndx="index"/>
        </div>
    </div>
</template>

<script setup>
    import MatchTableRow from './MatchTableRow.vue'
    import { useGameInfoStore } from '../stores/gameInfoStore';
    import { usePlayerStore } from '../stores/gamePlayerStore';
    import { useDateStore } from '../stores/gameDateStore.js';
    import {teamname} from "../stores/teamInfo.js"
    import { ref } from 'vue';

    const store = useGameInfoStore();
    store.update();
    const playerStore = usePlayerStore();
    playerStore.update();

    const gameDateStore = useDateStore();
    gameDateStore.update();
    

        
</script>