export interface Child {
    id: number;
    fullName: string;
    birthDate: string;
}

export interface MedicalCard {
    id: number;
    child: Child | null;
    healthInfo: string;
    chronicDiseases: string;
    allergies: string;
    vaccinations: string;
    notes: string;
}