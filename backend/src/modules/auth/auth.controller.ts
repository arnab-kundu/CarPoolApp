import { Body, Controller, Post, ServiceUnavailableException } from '@nestjs/common';
import { SendOtpDto, VerifyOtpDto, RefreshDto } from './auth.dto';
/** Fail closed until provider-backed identity verification and revocable sessions exist. */
@Controller('auth') export class AuthController {
 @Post('send-otp') send(@Body() _body: SendOtpDto): never { return this.unavailable(); }
 @Post('verify-otp') verify(@Body() _body: VerifyOtpDto): never { return this.unavailable(); }
 @Post('refresh') refresh(@Body() _body: RefreshDto): never { return this.unavailable(); }
 @Post('logout') logout(): never { return this.unavailable(); }
 private unavailable(): never { throw new ServiceUnavailableException({code:'AUTH_NOT_CONFIGURED',message:'Identity provider and session persistence are not configured.'}); }
}
