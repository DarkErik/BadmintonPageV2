
import { ref } from 'vue';
import { getURL } from '../util';

const url = getURL('api/standings');

class StandingsStore {
    standings = ref([]);
    loading = ref(false);
    error = ref(null);
    lastUpdated = ref(null);

    async fetch() {
        this.loading.value = true;
        this.error.value = null;

        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(
                    `[FATAL] Could not fetch standings. HTTP ${response.status}`
                );
            }

            const json = await response.json();

            if (!Array.isArray(json)) {
                throw new Error('Invalid standings response.');
            }

            this.standings.value = json;

            this.lastUpdated.value = new Date();

        } catch (error) {
            console.error(error);
            this.error.value = error.message;

        } finally {
            this.loading.value = false;
        }
    }
}

// Singleton: Eine gemeinsame Instanz für die gesamte Anwendung
export const standingsStore = new StandingsStore();

// Direkter Zugriff auf die reaktiven Werte
export const standings = standingsStore.standings;
export const standingsLoading = standingsStore.loading;
export const standingsError = standingsStore.error;
export const standingsLastUpdated = standingsStore.lastUpdated;
