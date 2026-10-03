import { test } from 'node:test';
import assert from 'node:assert/strict';
import { Pool } from 'pg';
test('PostGIS route direction and proximity',{skip:!process.env.TEST_DATABASE_URL},async () => {
 const pool=new Pool({connectionString:process.env.TEST_DATABASE_URL});
 try {
  const result=await pool.query(`WITH route AS (SELECT ST_GeomFromText('LINESTRING(77.6 12.9,77.7 12.9,77.8 12.9)',4326) AS line),
  points AS (SELECT ST_SetSRID(ST_Point(77.65,12.9),4326) AS pickup,ST_SetSRID(ST_Point(77.75,12.9),4326) AS dropoff)
  SELECT ST_DWithin(line::geography,pickup::geography,2000) AS near,
  ST_LineLocatePoint(line,pickup)<ST_LineLocatePoint(line,dropoff) AS forward,
  ST_LineLocatePoint(line,dropoff)<ST_LineLocatePoint(line,pickup) AS reverse FROM route,points`);
  assert.equal(result.rows[0].near,true);assert.equal(result.rows[0].forward,true);assert.equal(result.rows[0].reverse,false);
 } finally {await pool.end();}
});
