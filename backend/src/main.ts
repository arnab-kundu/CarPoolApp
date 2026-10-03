import 'reflect-metadata';
import { NestFactory } from '@nestjs/core';
import { ValidationPipe } from '@nestjs/common';
import { randomUUID } from 'node:crypto';
import { AppModule } from './app.module';
async function bootstrap() {
 const app = await NestFactory.create(AppModule);
 app.setGlobalPrefix('v1');
 app.use((req: any, res: any, next: () => void) => {
  const id = randomUUID(); req.requestId = id; res.setHeader('X-Request-ID',id);
  res.on('finish',() => console.log(JSON.stringify({requestId:id, method:req.method,path:req.path,status:res.statusCode})));
  next();
 });
 app.useGlobalPipes(new ValidationPipe({whitelist:true,forbidNonWhitelisted:true,transform:true}));
 await app.listen(Number(process.env.PORT ?? 3000),'0.0.0.0');
}
void bootstrap();
