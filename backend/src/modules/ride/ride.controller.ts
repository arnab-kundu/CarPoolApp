import { Body, Controller, Get, Post, Param, ParseUUIDPipe, ServiceUnavailableException } from '@nestjs/common';
import { PublishRideDto, SearchRidesDto } from './ride.dto';
@Controller('rides') export class RideController {
 @Post() publish(@Body() _body: PublishRideDto): never { return this.unavailable(); }
 @Post('search') search(@Body() _body: SearchRidesDto): never { return this.unavailable(); }
 @Get(':id') details(@Param('id',ParseUUIDPipe) _id: string): never { return this.unavailable(); }
 private unavailable(): never { throw new ServiceUnavailableException({code:'RIDES_NOT_CONFIGURED',message:'Authentication, verification, route provider, and persistence adapters are pending.'}); }
}
