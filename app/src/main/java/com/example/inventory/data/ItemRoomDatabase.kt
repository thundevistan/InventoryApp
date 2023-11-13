package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * 1. 데이터베이스 홀더를 포함하며 앱의 지속적인 관계형 데이터의 기본 연결을 위한 기본 액세스 포인트 역할을 한다
 * 2. @Database로 주석이 지정된 클래스는 다음 조건을 충족해야 한다
 *      1. RoomDatabase를 상속하는 추상 클래스여야 한다
 *      2. 주석 내에 데이터베이스와 연결된 Entity의 목록을 포함해야 한다
 *      3. 인자가 0개이며 @Dao로 주석이 지정된 클래스를 반환하는 추상 메서드를 포함해야 한다
 *
 * 3. 런타임 시 Room.databaseBuilder() 또는 Room.inMemoryDatabaseBuilder()를 호출하여 Database 인스턴스를 가져올 수 있습니다.
 * 4. 하나의 RoomDatabase 인스턴스만 필요함으로 RoomDatabase를 싱글톤으로 선언
 */
@Database(
	entities = [Item::class],
	version = 1,
	exportSchema = false        // 스키마 버전 기록 백업 안함
)
abstract class ItemRoomDatabase : RoomDatabase() {      // 구현부는 Room이 구현하기 때문에 추상 클래스로 선언
	abstract fun itemDao(): ItemDao     // DAO 를 반환하는 추상 함수를 선언, 여러 개가 있을 수 있다

	companion object {
		@Volatile       // 모든 쓰기와 읽기는 주 메모리에서 수행 -> 모든 실행 스레드에서 INSTANCE의 값이 항상 최신 상태로 유지되고 동일하게 유지 -> 즉, 한 스레드가 INSTANCE를 변경하면 다른 모든 스레드에서 즉시 확인할 수 있다
		private var INSTANCE: ItemRoomDatabase? = null      // INSTANCE 변수를 null 로 초기화: 데이터베이스가 만들어지면 데이터베이스 참조를 유지 -> 이렇게 하면 생성 및 유지 관리 비용이 많이 드는 데이터베이스의 단일 인스턴스를 유지 관리하는 데 도움이 된다

		// INSTANCE 가 null 일 경우 INSTANCE 를 초기화 후 반환
		fun getDatabase(context: Context): ItemRoomDatabase {
			return INSTANCE ?: synchronized(this) {     // synchronized: 데이터베이스가 한 번만 초기화되도록 제어
				val instance = Room.databaseBuilder(
					context.applicationContext,
					ItemRoomDatabase::class.java,
					"item_database"
				)
					.fallbackToDestructiveMigration()
					.build()

				INSTANCE = instance
				return instance
			}
		}
	}
}