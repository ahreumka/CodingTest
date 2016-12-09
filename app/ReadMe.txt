1) 화면구성
MainActivity는 tablayout을 가지고 있으며
각각의 탭은 현재 값을 보여주는 만보기화면 FragmentForMonitorScreen /만보기 기록 FragmentForRecordList로 구성됩니다.


2) 기본동작
사용자가 monitor화면에서 Start버튼을 누르면 단말의 sensor에서 stepCount를 가져옵니다.
stop되었을 때 센서값이 계속 늘어나는 것을 방지하기 위해 sharedpreference를 사용 합니다.

3) 이동거리
발걸음 * 보폭으로 거리를 계산하며 현재는 0.8m로 설정되어있으며
추후에 사용자가 본인의 보폭을 설정할 수 있도록 기능확장을 고려했습니다.

4) 자료저장
ContentProvider를 사용하여 저장된 내용을 공유할 수 있습니다.
AlarmManager 사용하여 1일에 한 번 현재까지의 기록을 DB에 저장합니다.
stop버튼을 눌러 종료 할 시에는 알람매니저는 해제됩니다.

----------------------------------------------------------------------------
finalCommit이후의 commit은 개인 공부차원으로 수정하고 정리하여 업뎃을 하고자 합니다. 
참고부탁드립니다.
