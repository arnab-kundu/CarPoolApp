import { test } from 'node:test';
import assert from 'node:assert/strict';
import { canTransition,transition,seatContribution,BookingState } from '../src/domain/state-machines';
test('cannot bypass payment or trip start',() => {
 assert.equal(canTransition('REQUESTED','CONFIRMED'),false);
 assert.equal(canTransition('CONFIRMED','COMPLETED'),false);
 assert.throws(() => transition('REQUESTED','COMPLETED'),/INVALID_BOOKING_TRANSITION/);
});
test('happy path and terminal states',() => {
 const sequence: BookingState[]=['REQUESTED','ACCEPTED','PAYMENT_PENDING','CONFIRMED','TRIP_STARTED','COMPLETED'];
 for(let i=1;i<sequence.length;i++) assert.equal(transition(sequence[i-1],sequence[i]),sequence[i]);
 assert.equal(canTransition('COMPLETED','REQUESTED'),false);
 assert.equal(canTransition('REFUNDED','CONFIRMED'),false);
});
test('prices use integer minor units and reject invalid inputs',() => {
 assert.equal(seatContribution(3,12500),37500);
 assert.equal(seatContribution(1,0),0);
 for(const [seats,price] of [[0,100],[7,100],[1,-1],[1,1.5],[6,Number.MAX_SAFE_INTEGER]]) assert.throws(() => seatContribution(seats,price));
});
