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
    userId: number;
    username: string;
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

export interface MedicalVisit {
    id: number;
    date: string;
    child: Child;
    doctor: Employee;
    description: string;
    recommendations: string;
    medications: string;
}

export interface Notification {
    id: number;
    recipient: User;
    type: string;
    subject: string;
    message: string;
    status: string;
    createdAt: string;
    sentAt: string;
}

export interface Voucher {
    id: number;
    child: Child;
    session: CampSession;
    status: 'BOOKED' | 'CONFIRMED' | 'CANCELLED' | 'USED';
    bookingDate: string;
}

export interface Payment {
    id: number;
    parent: User;
    voucher: Voucher;
    amount: number;
    date: string;
    status: 'paid' | 'pending' | 'cancelled';
    method: 'credit_card' | 'cash' | 'bank_transfer';
    comment?: string | null;
}

export interface ActivityLog {
    id: number;
    user: User;
    action: string;
    timestamp: string;
    description: string;
}

export interface DutyLog {
    id: number;
    date: string;
    employee: Employee;
    session: CampSession;
    startTime: string;
    endTime: string;
    location: string;
    status: string;
    description: string;
    notes: string;
    report: string;
}

export interface Schedule {
    id: number;
    date: string;
    session: CampSession;
    employee: Employee;
    time: string;
    title: string;
    description: string;
    location: string;
    team: string;
}