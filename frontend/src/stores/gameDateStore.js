import {defineStore} from "pinia"
import {getURL} from "../util"


export const useDateStore = defineStore("dates", {
    state: () => {
        return {
            games: [
                null,
                                null,                null,                null,                null,                null,                null,                null,                null,                null,                null,                null,                null,                null,
                null
            ]
        };
    },
    actions: {
         async update() {
            const url = getURL("api/game-dates");
            try {
                const response = await fetch(url);
                if (!response.ok) {
                    throw new Error(`[FATAL] Could not fetch players.`);
                }
                const gameJson = await response.json();
                //console.log(JSON.stringify(gameJson));
                console.log("Updated date info:" );
                this.games = gameJson;
                // usePlayerStore().setGameAmount(gameJson.length);
                console.log(this.games);
            } catch (error) {
                console.error(error.message);
            }

        },

        async changeDate(date, gameIndx) {
           
            const url = getURL("api/game-dates/" + gameIndx);
            try {
                const response = await fetch(url,
                    {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify({
                            date: date
                        })
                    }
                );
                if (!response.ok) {
                    throw new Error(`[FATAL] Could not add player.`);
                }

                const gameJson = await response.json();
                console.log("Updated date info");
                this.games = gameJson;
            } catch (error) {
                console.error(error.message);
            }
        },
        async approve(gameIndx) {
            const url = getURL("api/game-dates/approvals/" + gameIndx);
            try {
                const response = await fetch(url,
                    {
                        method: "POST"
                    }
                );
                if (!response.ok) {
                    throw new Error(`[FATAL] Could not add player.`);
                }

                const gameJson = await response.json();
                console.log("Updated date info");
                this.games = gameJson;
            } catch (error) {
                console.error(error.message);
            }
        },
        
        setGameAmount(amount) {
            while(this.games.length < amount) {
                this.games.push(
                    null

                )
            }
        }
    },
    getters: {

    }


})
