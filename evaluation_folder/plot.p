# Line width of the axes
set border linewidth 1.5
# Line styles
set style line 1 linecolor rgb '#0060ad' linetype 1 linewidth 2
set style line 2 linecolor rgb '#dd181f' linetype 1 linewidth 2
set style line 3 linecolor rgb '#F0681f' linetype 1 linewidth 2

set ylabel "P, R, F1"
set yrange [0:1.1]
set datafile separator "\t"

#pretpostavka da je sortirano krecuci sa bits-4-blocks-4 bits-4-blocks-16 ...
set multiplot layout 3,3 rowsfirst
#plot "dataset7GNU.txt" index 0 using 1:2 title 'Razred 1' with points pointtype 1, \
#     "dataset7GNU.txt" index 1 using 1:2 title 'Razred 2' with points pointtype 2, \
#     "dataset7GNU.txt" index 2 using 1:2 title 'Razred 3' with points pointtype 5	

#plot for [i=3:5]  using 2:i path index 0 with lines  

path = "results_precision_recall_f1.txt"
xlab = "Jaccard distance threshold"

set xlabel xlab
set ylabel "P, R, F1"
set yrange [0:1.1]

#set origin 0.0,0.0

set title "bits-4-blocks-4"

plot path every :::0::0 using 2:3 title 'Precision' with lines linestyle 1, \
     path every :::0::0 using 2:4 title 'Recall' with lines linestyle 2, \
     path every :::0::0 using 2:5 title 'F1' with lines linestyle 3

set xlabel xlab
set ylabel "P, R, F1"
set yrange [0:1.1]

set title "bits-4-blocks-16"
	 
plot path every :::1::1 using 2:3 title 'Precision' with lines linestyle 1, \
     path every :::1::1 using 2:4 title 'Recall' with lines linestyle 2, \
     path every :::1::1 using 2:5 title 'F1' with lines linestyle 3

set xlabel xlab
set ylabel "P, R, F1"
set yrange [0:1.1]

set title "bits-4-blocks-32"
	 
plot path every :::2::2 using 2:3 title 'Precision' with lines linestyle 1, \
     path every :::2::2 using 2:4 title 'Recall' with lines linestyle 2, \
     path every :::2::2 using 2:5 title 'F1' with lines linestyle 3

set xlabel xlab
set ylabel "P, R, F1"
set yrange [0:1.1]

set title "bits-6-blocks-4"
	 
plot path every :::3::3 using 2:3 title 'Precision' with lines linestyle 1, \
     path every :::3::3 using 2:4 title 'Recall' with lines linestyle 2, \
     path every :::3::3 using 2:5 title 'F1' with lines linestyle 3

set xlabel xlab
set ylabel "P, R, F1"
set yrange [0:1.1]

set title "bits-6-blocks-16"
	 
plot path every :::4::4 using 2:3 title 'Precision' with lines linestyle 1, \
     path every :::4::4 using 2:4 title 'Recall' with lines linestyle 2, \
     path every :::4::4 using 2:5 title 'F1' with lines linestyle 3

set xlabel xlab
set ylabel "P, R, F1"
set yrange [0:1.1]

set title "bits-6-blocks-32"
	 
plot path every :::5::5 using 2:3 title 'Precision' with lines linestyle 1, \
     path every :::5::5 using 2:4 title 'Recall' with lines linestyle 2, \
     path every :::5::5 using 2:5 title 'F1' with lines linestyle 3

set xlabel xlab
set ylabel "P, R, F1"
set yrange [0:1.1]

set title "bits-7-blocks-4"
plot path every :::6::6 using 2:3 title 'Precision' with lines linestyle 1, \
     path every :::6::6 using 2:4 title 'Recall' with lines linestyle 2, \
     path every :::6::6 using 2:5 title 'F1' with lines linestyle 3
	 
set xlabel xlab
set ylabel "P, R, F1"
set yrange [0:1.1]
set title "bits-7-blocks-16"
	 
plot path every :::7::7 using 2:3 title 'Precision' with lines linestyle 1, \
     path every :::7::7 using 2:4 title 'Recall' with lines linestyle 2, \
     path every :::7::7 using 2:5 title 'F1' with lines linestyle 3
set xlabel xlab
set ylabel "P, R, F1"
set yrange [0:1.1]

set title "bits-7-blocks-32"
	 
plot path every :::8::8 using 2:3 title 'Precision' with lines linestyle 1, \
     path every :::8::8 using 2:4 title 'Recall' with lines linestyle 2, \
     path every :::8::8 using 2:5 title 'F1' with lines linestyle 3
     
	 
	 
	 
	 
unset multiplot