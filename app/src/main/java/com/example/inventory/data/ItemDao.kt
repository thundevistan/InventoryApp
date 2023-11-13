package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * 1. DAO (데이터 액세스 객체): 데이터베이스에 액세스하는 인터페이스를 정의하는 Room의 기본 구성요소
 * 2. Room 은 컴파일 시에 이 클래스의 구현을 생성
 */
@Dao
interface ItemDao {
	@Insert(onConflict = OnConflictStrategy.IGNORE)     // onConflict: 충돌이 발생하는 경우 Room 이 실행할 작업을 알려준다
	suspend fun insert(item: Item)                      // OnConflictStrategy.IGNORE: 기본 키가 이미 데이터베이스에 있으면 새 데이터를 무시

	@Update
	suspend fun update(item: Item)

	@Delete
	suspend fun delete(item: Item)

	@Query("SELECT * from item WHERE id = :id")     // id = :id -> 콜론 표기법을 사용하여 함수의 인수를 참조
	fun getItem(id: Int): Flow<Item>                // Flow 또는 LiveData 를 반환 유형으로 사용하면 suspend 함수로 만들고 코루틴 범위 내에서 호출할 필요는 없다

	@Query("SELECT * from item ORDER BY name ASC")
	fun getItems(): Flow<List<Item>>
}