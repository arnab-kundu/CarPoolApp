import { Type } from 'class-transformer';
import { IsDefined, IsInt, IsNumber, IsString, IsISO8601, IsUUID, Min, Max, MaxLength, ValidateNested, IsOptional } from 'class-validator';
export class LocationDto {
 @IsNumber() @Min(-90) @Max(90) latitude!: number;
 @IsNumber() @Min(-180) @Max(180) longitude!: number;
 @IsString() @MaxLength(300) address!: string;
 @IsString() @MaxLength(300) placeId!: string;
}
export class PublishRideDto {
 @IsUUID() vehicleId!: string;
 @IsDefined() @ValidateNested() @Type(() => LocationDto) origin!: LocationDto;
 @IsDefined() @ValidateNested() @Type(() => LocationDto) destination!: LocationDto;
 @IsISO8601({strict:true}) departureTime!: string;
 @IsInt() @Min(1) @Max(6) availableSeats!: number;
 @IsInt() @Min(0) @Max(1000000) contributionMinor!: number;
}
export class SearchRidesDto {
 @IsDefined() @ValidateNested() @Type(() => LocationDto) origin!: LocationDto;
 @IsDefined() @ValidateNested() @Type(() => LocationDto) destination!: LocationDto;
 @IsISO8601({strict:true}) departureTime!: string;
 @IsInt() @Min(1) @Max(6) seats!: number;
 @IsOptional() @IsInt() @Min(1) @Max(100) limit = 20;
 @IsOptional() @IsString() @MaxLength(200) cursor?: string;
}
