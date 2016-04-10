package Import

/**
  * Created by yannick on 26.02.16.
  */
object TestData {
  val validPlayers = List(
    "Clemens Niemeyer,TST,m,27,7,179,5,a,b",
    "Sophie Rossteuscher,TST,w,25,6,174,11,a,b",
    "Jürgen Winter,TST,m,32,8,181,13,a,b",
    "Jonathan Rohland,TST,m,26,5,185,2,a,b",
    "Florian Lindemann,TST,m,32,8,182,14,a,b",
    "Harald Imhof,TST,m,29,7,180,6,a,b",
    "Grigory Movsesyan (Grisha),TST,m,29,5,182,16,a,b",
    "Maria Chebotarenko,TST,w,26,5,165,4,a,b",
    "Isabella Spieler,TST,w,28,8,162,15,a,b",
    "Lorenz Kniep,TST,m,31,8,182,8,a,b",
    "Felix Vogt,TST,m,27,7,180,12,a,b",
    "Michael Remy,TST,m,29,9,181,20,a,b",
    "Verena Plehn,TST,w,27,8,167,15,a,b",
    "Immo Prenzel,TST,m,29,8,180,15,a,b",
    "Thomas Hartmann,TST,m,30,8,190,15,a,b",
    "Barbara Schmitt-Sody,TST,w,31,7,166,10,a,b",
    "Tatjana Tertsch,TST,w,24,3,160,3,a,b",
    "Anian Richter,TST,m,26,5,187,7,a,b",
    "Magdalena Dinkel,TST,w,27,4,165,5,a,b",
    "Felix Hagemeister,TST,m,26,8,186,8,a,b",
    "Anna-Maria Haas,TST,w,27,5,160,5,a,b",
    "Wolfgang Schanderl,TST,m,30,6,199,13,a,b",
    "Leon Hartung,TST,m,24,3,170,3,a,b",
    "Malte Wilms,TST,m,30,4,170,4,a,b",
    "Andreas Heuwieser,TST,m,27,6,170,7,a,b",
    "Rafael Preosck,TST,m,24,3,170,3,a,b",
    "Sören Ungermann,TST,m,30,8,175,11,a,b",
    "Christine Dießl,TST,w,29,7,159,11,a,b",
    "Verena Spieler,TST,w,28,9,162,15,a,b",
    "Sebastian Zier (Oz),TST,m,30,6,183,8,a,b",
    "Tom Kingdom ,TST,m,30,6,180,5,a,b",
    "Saskia Poesze,TST,w,25,5,173,9,a,b",
    "Robert Hölzel,TST,m,31,8,181,7,a,b",
    "Patrick Zech,TST,m,26,5,181,5,a,b")

  val brokenPlayers = List(
    "Clemens",
    "Yannick,None,m,26,1,187,0,1,,")
  val brokenPlayersTwo = List(
    "Clemens",
    "Yannick,None,m,2sd6,1,fds187,0",
    "Yannick,None,man,26,1,187,0"
  )
  val validPlayers2 = List(
    "Clemens,Tiefseetaucher,m,27,10,179,7,1,,",
    "Yannick,None,m,26,1,187,0,1,,",
    "Tom,Tiefseetaucher,w,47,1,199,34,1,,",
    "Alice,None,male,12,15,120,1,1,,",
    "Bob,Tiefseetaucher,m,19,3,159,2,1,,",
    "Sara,None,w,16,1,163,0,1,,",
    "Svenjamin,Tiefseetaucher,m,27,10,179,7,1,,",
    "Benjamin,None,female,26,1,187,0,1,,"
  )
  val validPlayersShort = List(
    "A,ATeam,w,2,2,8,2,2,B,",
    "B,BTeam,m,2,2,2,2,2,,A",
    "C,ATeam,w,2,2,2,2,2,A,D",
    "D,BTeam,m,2,2,8,2,2,,"
  )
}
