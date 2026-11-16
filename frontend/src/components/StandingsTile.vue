
<template>
    <div class="w-full overflow-hidden rounded-xl border border-gray-200 bg-white shadow-2xl">

        <!-- Header -->
        <div class="p-3">
            <div class="flex items-center justify-between">
                <div class="text-lg font-semibold text-gray-800">
                    Ligatabelle
                </div>

            </div>

            <div class="mt-1 text-sm text-gray-500">
                {{ standings.length }} Mannschaften
            </div>
        </div>

        <!-- Loading -->
        <div
            v-if="standingsLoading && standings.length === 0"
            class="p-8 text-center text-gray-500"
        >
            Tabelle wird geladen...
        </div>

        <!-- Error -->
        <div
            v-else-if="standingsError && standings.length === 0"
            class="p-5 text-center text-red-600"
        >
            Tabelle konnte nicht geladen werden.

            <button
                @click="standingsStore.fetch()"
                class="mt-2 block w-full text-sm underline"
            >
                Erneut versuchen
            </button>
        </div>

        <!-- Empty -->
        <div
            v-else-if="standings.length === 0"
            class="p-8 text-center text-gray-500"
        >
            Keine Tabellendaten vorhanden.
        </div>

        <template v-else>

            <!-- DESKTOP: Klassische Tabelle -->
            <div class="hidden sm:block px-3 pb-3">
                <table class="w-full table-fixed text-left text-base">

                    <thead class="bg-gray-100 text-gray-700">
                        <tr>
                            <th class="w-14 p-3 text-center">Pl.</th>
                            <th class="p-3">Mannschaft</th>
                            <th class="w-20 p-3 text-center">Sp.</th>
                            <th class="w-28 p-3 text-center">Punkte</th>
                        </tr>
                    </thead>

                    <tbody class="divide-y divide-gray-200">

                        <tr
                            v-for="team in standings"
                            :key="team.displayRank + '-' + team.clubName"
                            :class="[
                                isOwnClub(team)
                                    ? 'bg-blue-200 font-semibold'
                                    : team.displayRank % 2 === 0
                                        ? 'bg-white'
                                        : 'bg-blue-50',
                                'hover:bg-blue-100 transition-colors'
                            ]"
                        >
                            <td class="p-3 text-center">
                                {{ team.displayRank }}.
                            </td>

                            <td class="p-3 break-words">
                                {{ team.clubName }}

                                <span
                                    v-if="isOwnClub(team)"
                                    class="ml-1 text-xs text-blue-700"
                                >
                                    (Wir)
                                </span>
                            </td>

                            <td class="p-3 text-center tabular-nums">
                                {{ team.playedGames }}
                            </td>

                            <td class="p-3 text-center tabular-nums">
                                {{ team.wonPoints }}:{{ team.lostPoints }}
                            </td>
                        </tr>

                    </tbody>
                </table>
            </div>

            <!-- MOBILE: Kompakte Mannschaftskacheln -->
            <div class="sm:hidden space-y-2 px-3 pb-3">

                <div
                    v-for="team in standings"
                    :key="team.displayRank + '-' + team.clubName"
                    :class="[
                        isOwnClub(team)
                            ? 'bg-blue-200 border-blue-300'
                            : team.displayRank % 2 === 0
                                ? 'bg-white border-gray-200'
                                : 'bg-blue-50 border-blue-100',
                        'flex items-center gap-3 rounded-lg border p-3'
                    ]"
                >

                    <!-- Rang -->
                    <div
                        class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-white text-lg font-bold text-gray-700 shadow-sm"
                    >
                        {{ team.displayRank }}
                    </div>

                    <!-- Mannschaft -->
                    <div class="min-w-0 flex-1">

                        <div class="break-words font-medium text-gray-800">
                            {{ team.clubName }}
                        </div>

                        <div
                            v-if="isOwnClub(team)"
                            class="text-xs font-semibold text-blue-700"
                        >
                            Unser Verein
                        </div>

                        <div class="mt-1 text-xs text-gray-500">
                            {{ team.playedGames }} Spiele
                        </div>

                    </div>

                    <!-- Punkte -->
                    <div class="shrink-0 text-right">
                        <div class="text-xs text-gray-500">
                            Punkte
                        </div>

                        <div class="text-lg font-bold tabular-nums text-gray-800">
                            {{ team.wonPoints }}:{{ team.lostPoints }}
                        </div>
                    </div>

                </div>

            </div>

            <!-- Footer -->
            <div class="border-t border-gray-100 px-3 py-2 text-xs text-gray-400">
                <span v-if="standingsLastUpdated">
                    Aktualisiert:
                    {{ formatUpdated(standingsLastUpdated) }}
                </span>
                <span v-else>
                    Noch nicht aktualisiert
                </span>
            </div>

        </template>
    </div>
</template>

<script setup>
import { onMounted } from 'vue';

import {
    standingsStore,
    standings,
    standingsLoading,
    standingsError,
    standingsLastUpdated
} from '../stores/standingsStore';

// Name der eigenen Mannschaft
const OWN_CLUB = 'TG Bochum 1884';

function isOwnClub(team) {
    return team.clubName.startsWith(OWN_CLUB);
}

// Letzter Aktualisierungszeitpunkt
function formatUpdated(date) {
    return new Intl.DateTimeFormat('de-DE', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }).format(date);
}

// Tabelle beim ersten Anzeigen laden
onMounted(() => {
    standingsStore.fetch();
});
</script>
