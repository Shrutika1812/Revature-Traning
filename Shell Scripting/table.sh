echo "Enter a num:"
read n

echo "Multiplication Table of $n"

for i in {1..10}
do
	echo "$n * $i = $((n * i))"
done


