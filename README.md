# CodingTest

1) 화면구성

MainActivity는 tabLayout을 가지고 있으며
각각의 탭은 현재 값을 보여주는 만보기 화면 FragmentForMonitorScreen / 만보기 기록 FragmentForRecordList로 구성됩니다.

2) 기본동작

사용자가 monitor화면에서 Start버튼을 누르면 StepCountSevice를 시작하여 stepCount를 구해 화면서 뿌려주도록 합니다.
min sdk 15를 지원하기 위해 Sonsor.TYPE_ACCELEROMETER로 구현되었습니다.

3) 이동거리

발걸음 * 보폭으로 거리를 계산하며 현재는 0.8m로 설정되어있으며
추후에 사용자가 본인의 보폭을 설정할 수 있도록 기능확장을 고려했습니다.

4) 자료저장

ContentProvider를 사용하여 저장된 내용을 공유할 수 있습니다.
AlarmManager 사용하여 1일에 한 번 현재까지의 기록을 DB에 저장합니다.
stop버튼을 눌러 종료 할 시에는 알람매니저는 해제됩니다.

5) 위치정보

LocationManager로 부터 Latitude, ngitude를 구해 서버와 연동하여 위치정보를 가져옵니다.

----------------------------------------------------------------------------
개인 공부차원으로 finalCommit이후 업뎃을 하고자 합니다. 
