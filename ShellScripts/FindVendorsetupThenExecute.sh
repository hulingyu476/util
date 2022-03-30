# Execute the contents of any vendorsetup.sh files we can find.
for f in `test -d device  && find -L device  -maxdepth 4 -name 'vendorsetup.sh' 2> /dev/null | sort` \
         `test -d vendor  && find -L vendor  -maxdepth 4 -name 'vendorsetup.sh' 2> /dev/null | sort` \
         `test -d product && find -L product -maxdepth 4 -name 'vendorsetup.sh' 2> /dev/null | sort`
do
    echo "including $f"
    . $f
done
unset f
