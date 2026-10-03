require('reflect-metadata');
const {test}=require('node:test');
const assert=require('node:assert/strict');
const {NestFactory}=require('@nestjs/core');
const {ValidationPipe}=require('@nestjs/common');
const {AppModule}=require('../dist/app.module');
test('HTTP foundation validates input and fails closed',async()=>{
 const app=await NestFactory.create(AppModule,{logger:false});
 app.setGlobalPrefix('v1');
 app.useGlobalPipes(new ValidationPipe({whitelist:true,forbidNonWhitelisted:true,transform:true}));
 await app.listen(0,'127.0.0.1');
 const base=await app.getUrl();
 async function post(path,body) { return fetch(base+'/v1'+path,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)}); }
 try {
  const health=await fetch(base+'/v1/health');
  assert.equal(health.status,200);assert.equal((await health.json()).integrationsReady,false);
  assert.equal((await post('/auth/send-otp',{phone:'invalid'})).status,400);
  assert.equal((await post('/auth/send-otp',{phone:'+919876543210',verified:true})).status,400);
  const otp=await post('/auth/send-otp',{phone:'+919876543210'});
  assert.equal(otp.status,503);assert.equal((await otp.json()).code,'AUTH_NOT_CONFIGURED');
  assert.equal((await post('/rides/search',{departureTime:'2027-01-01T08:00:00Z',seats:1})).status,400);
  const origin={latitude:12.97,longitude:77.64,address:'Indiranagar',placeId:'sample-origin'};
  const destination={latitude:12.96,longitude:77.75,address:'Whitefield',placeId:'sample-destination'};
  const search=await post('/rides/search',{origin,destination,departureTime:'2027-01-01T08:00:00Z',seats:1});
  assert.equal(search.status,503);assert.equal((await search.json()).code,'RIDES_NOT_CONFIGURED');
  assert.equal((await post('/rides/search',{origin:{...origin,latitude:99},destination,departureTime:'2027-01-01T08:00:00Z',seats:1})).status,400);
  assert.equal((await fetch(base+'/v1/rides/not-a-uuid')).status,400);
 }finally {await app.close();}
});
