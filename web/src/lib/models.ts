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

export interface Employee {
    id: number;
    fullName: string;
    position: string;
}

export interface User {
    id: number;
    username: string;
    password: string;
    email: string;
    role: string;
    emailNotifications: boolean;
    smsNotifications: boolean;
    theme: string;
    children: Child[];
    employee: Employee;
}

export interface CampSession {
    id: number;
    name: string;
    startDate: string;
    endDate: string;
    description: string;
    maxChildren: number;
    price: number;
}

export interface Menu {
  id: number;
  date: string;
  session: CampSession;
  breakfast: string;
  lunch: string;
  dinner: string;
  notes: string;
}