import { IsString, Matches, Length } from 'class-validator';
export class SendOtpDto { @IsString() @Matches(/^\+[1-9]\d{7,14}$/) phone!: string; }
export class VerifyOtpDto {
 @IsString() @Length(1,200) challengeId!: string;
 @IsString() @Matches(/^\d{6}$/) code!: string;
}
export class RefreshDto { @IsString() @Length(20,2048) refreshToken!: string; }
