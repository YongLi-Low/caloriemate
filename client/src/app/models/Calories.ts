export interface Calories {
    id: number;
    foreignId: string; // this is the user's id
    username: string;
    foodName: string;
    quantity: number;
    calories: number;
    entryDate: string;
}