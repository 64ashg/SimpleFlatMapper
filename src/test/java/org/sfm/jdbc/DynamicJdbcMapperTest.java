package org.sfm.jdbc;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbObject.Type;
import org.sfm.utils.ListHandler;
import org.sfm.utils.RowHandler;

public class DynamicJdbcMapperTest {
	
	final DynamicJdbcMapper<DbObject> mapper;
	
	public DynamicJdbcMapperTest() throws NoSuchMethodException, SecurityException, SQLException {
		mapper = (DynamicJdbcMapper<DbObject>) JdbcMapperFactory.newInstance().useAsm(false).newMapper(DbObject.class);
	}
	
	@Test
	public void testResultSetMapperForEachRS()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				List<DbObject> objects = mapper.forEach(ps.executeQuery(), new ListHandler<DbObject>()).getList();
				assertEquals(1, objects.size());
				DbHelper.assertDbObjectMapping(objects.get(0));
			}
		});
	}
	
	@Test
	public void testResultSetMapperMap()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				rs.next();
				DbObject object = mapper.map(rs);
				DbHelper.assertDbObjectMapping(object);
			}
		});
	}
	
	
	private static final int NBROW = 2;
	private static final int NBFUTURE = 10000;
	@Test
	public void testMultipleThread() throws InterruptedException, ExecutionException {
		final DynamicJdbcMapper<DbObject> mapper = (DynamicJdbcMapper<DbObject>) JdbcMapperFactory.newInstance().useAsm(true).newMapper(DbObject.class);
		
		ExecutorService service = Executors.newFixedThreadPool(4);
		final AtomicLong sumOfAllIds = new AtomicLong();
		final AtomicLong nbRow = new AtomicLong();
		
		final RowHandler<DbObject> handler = new RowHandler<DbObject>() {
			@Override
			public void handle(DbObject t) throws Exception {
				long id = t.getId();
				
				assertEquals("name" + Long.toHexString(id), t.getName());
				assertEquals("email" + Long.toHexString(id), t.getEmail());
				assertEquals(Type.values()[(int)(id) % 4], t.getTypeName());
				assertEquals(Type.values()[(int)(id) % 4], t.getTypeOrdinal());
				assertEquals(id, t.getCreationTime().getTime() / 1000);
				
				sumOfAllIds.addAndGet(id);
				nbRow.incrementAndGet();
			}
		};
		
		
		List<Future<Object>> futures = new ArrayList<Future<Object>>(); 
		for(int i = 0; i < NBFUTURE; i++) {
			futures.add(service.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					mapper.forEach(new MockDbObjectResultSet(NBROW), handler);
					return null;
				}
			}));
		}
		
		
		int i = 0;
		for(Future<Object> future : futures) {
			try {
				future.get();
			}  catch(Exception e) {
				System.out.println("Future " + i + " fail " + e);
			}
			i++;
		}
		assertEquals(NBFUTURE, i);
		assertEquals(nbRow.get(), NBFUTURE * NBROW);
		
		int sum = 0;
		for(i = 1 ; i <= NBROW ; i++) {
			sum += i;
		}
		
		assertEquals(NBFUTURE * sum, sumOfAllIds.get());
	}
}
