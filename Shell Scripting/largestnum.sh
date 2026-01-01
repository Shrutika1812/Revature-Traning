echo  "enter first num:"
read a

echo "enter second num:"
read b

echo "enter third num:"
read c

if [ $a -gt $b ] && [ $a -gt $c ]; then
echo "Largest num is: $a"

elif [ $b -gt $a ] && [ $b -gt $c ]; then
echo "Largest num is: $b"

else
echo "Largest num is: $c:"
fi

