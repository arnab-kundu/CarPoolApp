export type BookingState = 'REQUESTED'|'ACCEPTED'|'PAYMENT_PENDING'|'CONFIRMED'|'TRIP_STARTED'|'COMPLETED'|'REJECTED'|'CANCELLED_BY_DRIVER'|'CANCELLED_BY_PASSENGER'|'NO_SHOW'|'PAYMENT_FAILED'|'REFUNDED';
const transitions: Partial<Record<BookingState,BookingState[]>> = {
 REQUESTED:['ACCEPTED','REJECTED','CANCELLED_BY_PASSENGER','CANCELLED_BY_DRIVER'],
 ACCEPTED:['PAYMENT_PENDING','CANCELLED_BY_DRIVER','CANCELLED_BY_PASSENGER'],
 PAYMENT_PENDING:['CONFIRMED','PAYMENT_FAILED','CANCELLED_BY_DRIVER','CANCELLED_BY_PASSENGER'],
 CONFIRMED:['TRIP_STARTED','NO_SHOW','CANCELLED_BY_DRIVER','CANCELLED_BY_PASSENGER'],
 TRIP_STARTED:['COMPLETED'],PAYMENT_FAILED:['PAYMENT_PENDING','CANCELLED_BY_PASSENGER'],
 CANCELLED_BY_DRIVER:['REFUNDED'],CANCELLED_BY_PASSENGER:['REFUNDED']
};
export function canTransition(from: BookingState,to: BookingState): boolean { return transitions[from]?.includes(to) ?? false; }
export function transition(from: BookingState,to: BookingState): BookingState {
 if(!canTransition(from,to)) throw new Error('INVALID_BOOKING_TRANSITION'); return to;
}
export function seatContribution(seats: number,unitPriceMinor: number): number {
 if(!Number.isSafeInteger(seats)||seats<1||seats>6||!Number.isSafeInteger(unitPriceMinor)||unitPriceMinor<0) throw new Error('INVALID_PRICE_INPUT');
 const total=seats*unitPriceMinor;
 if(!Number.isSafeInteger(total)) throw new Error('PRICE_OVERFLOW');
 return total;
}
