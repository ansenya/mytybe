import { Visibility, VisibilityOff } from "@mui/icons-material";
import { IconButton, InputAdornment, TextField } from "@mui/material";
import React from "react";


interface AuthFormFieldProps {
    label: string;
    onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

const AuthFormField = (props: AuthFormFieldProps) => {
    const [showPassword, setShowPassword] = React.useState(false);
    const toCamelCase = (word: string) => word[0].toUpperCase() + word.slice(1);
    const handleClickShowPassword = () => setShowPassword((show) => !show);
    const handleMouseDownPassword = (event: React.MouseEvent<HTMLButtonElement>) => {
        event.preventDefault();
    };

    return (
        <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            id={props.label}
            label={toCamelCase(props.label)}
            name={props.label}
            autoComplete={props.label}
            autoFocus
            type={props.label==='password' && !showPassword ? "password" : "text"}
            InputLabelProps={{ style: { color: 'white' }}}    
            InputProps={{ 
                style: { color: 'white' },  
                endAdornment: props.label === 'password' ? (
                    <InputAdornment position="end">
                        <IconButton
                            aria-label="toggle password visibility"
                            onClick={handleClickShowPassword}
                            onMouseDown={handleMouseDownPassword}
                            style={{ color: 'white' }}>
                            {showPassword ? <Visibility /> : <VisibilityOff />}
                        </IconButton>
                    </InputAdornment>
                ) : null
            }}
            onChange={props.onChange}
            />
    );
}

export default AuthFormField;