import { Controller, Get, Module } from '@nestjs/common';
import { APP_GUARD } from '@nestjs/core';
import { ThrottlerModule, ThrottlerGuard } from '@nestjs/throttler';
import { AuthModule } from './modules/auth/auth.module';
import { UserModule } from './modules/user/user.module';
import { VehicleModule } from './modules/vehicle/vehicle.module';
import { VerificationModule } from './modules/verification/verification.module';
import { RideModule } from './modules/ride/ride.module';
import { MatchingModule } from './modules/matching/matching.module';
import { BookingModule } from './modules/booking/booking.module';
import { TripModule } from './modules/trip/trip.module';
import { PaymentModule } from './modules/payment/payment.module';
import { ChatModule } from './modules/chat/chat.module';
import { NotificationModule } from './modules/notification/notification.module';
import { RatingModule } from './modules/rating/rating.module';
import { SafetyModule } from './modules/safety/safety.module';
import { AdminModule } from './modules/admin/admin.module';
@Controller('health') class HealthController {
 @Get() health() { return {status:'ok',phase:'foundation',integrationsReady:false}; }
}
@Module({imports:[ThrottlerModule.forRoot([{ttl:60000,limit:60}]),AuthModule,UserModule,VehicleModule,VerificationModule,RideModule,MatchingModule,BookingModule,TripModule,PaymentModule,ChatModule,NotificationModule,RatingModule,SafetyModule,AdminModule],controllers:[HealthController],providers:[{provide:APP_GUARD,useClass:ThrottlerGuard}]})
export class AppModule {}
